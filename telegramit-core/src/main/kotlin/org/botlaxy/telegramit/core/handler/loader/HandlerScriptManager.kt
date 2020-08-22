package org.botlaxy.telegramit.core.handler.loader

import mu.KotlinLogging
import org.botlaxy.telegramit.core.extension.md5
import org.botlaxy.telegramit.core.extension.watch
import org.botlaxy.telegramit.core.handler.HandlerConstant
import org.botlaxy.telegramit.core.handler.dsl.ConversationHandler
import org.botlaxy.telegramit.core.handler.dsl.Handler
import org.botlaxy.telegramit.core.handler.loader.collect.ClassPathScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.DirectoryScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollectorException
import org.botlaxy.telegramit.core.handler.loader.compile.HandlerScriptCompiler
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

private val logger = KotlinLogging.logger {}

class HandlerScriptManager(
    private val handlerScriptCompiler: HandlerScriptCompiler,
    val handlerScriptDir: String?,
    val handlerHotReload: Boolean = false,
    private val scriptChangeListener: ((oldHandler: Handler?, newHandler: Handler) -> Unit)? = null
) {

    private val handlerScriptPath: Path = if (handlerScriptDir != null) {
        Paths.get(handlerScriptDir)
    } else {
        Paths.get(System.getProperty("user.dir")).resolve(HandlerConstant.HANDLERS_DIR)
    }

    private val handlerScriptCollector: ScriptCollector = if (handlerScriptDir != null) {
        DirectoryScriptCollector(handlerScriptDir)
    } else {
        ClassPathScriptCollector()
    }

    private var mutex = Object()

    @Volatile
    private var handlerWatchActive: Boolean = false

    private var handlerWatchThread: Thread? = null

    fun compileHandlerFiles(): List<Handler> {
        val handlerScriptFiles: List<Path>
        try {
            handlerScriptFiles = handlerScriptCollector.collect()
        } catch (e: ScriptCollectorException) {
            throw IllegalStateException("Exception during collecting scripts", e)
        }

        return if (handlerHotReload) {
            synchronized(mutex) {
                if (handlerWatchThread == null) {
                    // Calculate all files checksum
                    val handlerFileInfoMap = hashMapOf<String, HandlerFileInfo>()
                    for (handlerScriptFile in handlerScriptFiles) {
                        val checksum = handlerScriptFile.md5()
                        handlerFileInfoMap[checksum] = HandlerFileInfo(handlerScriptFile, checksum)
                    }

                    // Store file and handler relation
                    val fileHandlerMap = handlerScriptFiles.associateBy(
                        { path: Path -> path },
                        { path: Path -> handlerScriptCompiler.compile(path) }
                    ).toMutableMap()
                    for (handlerScriptFile in handlerScriptFiles) {
                        fileHandlerMap[handlerScriptFile] = handlerScriptCompiler.compile(handlerScriptFile)
                    }

                    // Watch handlers dir
                    val watchKeyMap = hashMapOf<WatchKey, Path>()
                    val watchService = handlerScriptPath.watch(StandardWatchEventKinds.ENTRY_MODIFY)
                    val simpleFileVisitor = object : SimpleFileVisitor<Path>() {
                        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                            val watchKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
                            watchKeyMap[watchKey] = dir
                            return FileVisitResult.CONTINUE;
                        }
                    }
                    Files.walkFileTree(handlerScriptPath, simpleFileVisitor)

                    handlerWatchActive = true
                    handlerWatchThread =
                        Thread(
                            HandlerWatch(watchService, watchKeyMap, handlerFileInfoMap, fileHandlerMap),
                            "HandlerWatchThread"
                        )
                    handlerWatchThread!!.isDaemon = true
                    handlerWatchThread!!.start()

                    return fileHandlerMap.map { it.value }
                }
                handlerScriptFiles.map { handlerScriptCompiler.compile(it) }
            }
        } else {
            handlerScriptFiles.map { handlerScriptCompiler.compile(it) }
        }
    }

    fun closeWatchHandler() {
        handlerWatchActive = false
        handlerWatchThread?.interrupt()
        handlerWatchThread?.join(10000)
    }

    private inner class HandlerWatch(
        val watchService: WatchService,
        val watchKeyMap: MutableMap<WatchKey, Path>,
        val handlerFileMap: MutableMap<String, HandlerFileInfo>,
        val fileHandlerMap: MutableMap<Path, Handler>
    ) : Runnable {

        override fun run() {
            watchService.use { watchService ->
                var watchKey: WatchKey
                while (watchService.take().also { watchKey = it } != null && handlerWatchActive) {
                    try {
                        val dir = watchKeyMap[watchKey]
                        if (dir == null) {
                            logger.error { "Can't resolve watchKey" }
                            continue
                        }
                        for (event in watchKey.pollEvents()) {
                            val path = handlerScriptPath.resolve(event.context() as Path)
                            val filePath = dir.resolve(path.fileName)

                            if (!Files.isRegularFile(filePath)) continue

                            val changedFileChecksum = filePath.md5()
                            val changedFile = filePath
                            val handlerFileInfo = handlerFileMap[changedFileChecksum]
                            if (handlerFileInfo != null) {
                                if (handlerFileInfo.filePath != changedFile) {
                                    // Maybe file name changed
                                    handlerFileMap[changedFileChecksum] =
                                        HandlerFileInfo(changedFile, changedFileChecksum)
                                }
                            } else {
                                val fileInfo = handlerFileMap.values.find { it.filePath == changedFile }
                                if (fileInfo != null) {
                                    handlerFileMap.remove(fileInfo.checksum) // Remove old file checksum
                                    handlerFileMap[changedFileChecksum] =
                                        HandlerFileInfo(changedFile, changedFileChecksum)
                                }
                                val oldHandler = fileHandlerMap[changedFile]
                                val newHandler = handlerScriptCompiler.compile(changedFile)
                                fileHandlerMap[changedFile] = newHandler
                                scriptChangeListener?.invoke(oldHandler, newHandler)
                            }
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "Unexpected exception during handler watch process" }
                    } finally {
                        watchKey.reset()
                    }
                }
            }
        }

    }

    private data class HandlerFileInfo(val filePath: Path, val checksum: String)

}
