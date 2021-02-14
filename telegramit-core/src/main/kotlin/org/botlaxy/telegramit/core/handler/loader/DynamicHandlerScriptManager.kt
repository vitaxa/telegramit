package org.botlaxy.telegramit.core.handler.loader

import mu.KotlinLogging
import org.botlaxy.telegramit.core.extension.md5
import org.botlaxy.telegramit.core.extension.watch
import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollectorException
import org.botlaxy.telegramit.core.handler.loader.compile.HandlerScriptCompiler
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.atomic.AtomicBoolean

private val logger = KotlinLogging.logger {}

class DynamicHandlerScriptManager(
    private val handlerScriptCompiler: HandlerScriptCompiler,
    private val scriptCollector: ScriptCollector
) : HandlerScriptManager {

    private var mutex = Object()

    private var handlerWatchThread: Thread? = null

    private val changeListenerList = mutableListOf<HandlerChangeListener>()

    private val compiledHandlerList = mutableListOf<TelegramHandler>()

    fun addHandlerChangeListener(listener: HandlerChangeListener) {
        changeListenerList.add(listener)
    }

    fun removeHandlerChangeListener(listener: HandlerChangeListener) {
        changeListenerList.remove(listener)
    }

    override fun compileScripts(): List<TelegramHandler> {
        try {
            val handlerScriptFiles: List<Path> = scriptCollector.collect()

            return if (handlerWatchThread == null) {
                val compiledScripts: List<TelegramHandler> = compileAndWatch(handlerScriptFiles)
                compiledHandlerList.addAll(compiledScripts)
                compiledScripts
            } else {
                compiledHandlerList
            }
        } catch (e: ScriptCollectorException) {
            throw IllegalStateException("Exception during collecting scripts", e)
        }
    }

    private fun compileAndWatch(scriptFiles: List<Path>): List<TelegramHandler> {
        synchronized(mutex) {
            // Get scripts parent directory
            val handlerScriptDir = scriptFiles.first().parent

            // Calculate all files checksum
            val handlerFileInfoMap = hashMapOf<String, HandlerFileInfo>()
            for (handlerScriptFile in scriptFiles) {
                val checksum = handlerScriptFile.md5()
                handlerFileInfoMap[checksum] = HandlerFileInfo(handlerScriptFile, checksum)
            }

            // Store file and handler relation
            val fileHandlerMap = scriptFiles.associateBy(
                { path: Path -> path },
                { path: Path -> handlerScriptCompiler.compile(path) }
            ).toMutableMap()
            for (handlerScriptFile in scriptFiles) {
                fileHandlerMap[handlerScriptFile] = handlerScriptCompiler.compile(handlerScriptFile)
            }

            // Watch handlers dir
            val watchKeyMap = hashMapOf<WatchKey, Path>()
            val watchService = handlerScriptDir.watch(StandardWatchEventKinds.ENTRY_MODIFY)
            val simpleFileVisitor = object : SimpleFileVisitor<Path>() {
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val watchKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
                    watchKeyMap[watchKey] = dir
                    return FileVisitResult.CONTINUE;
                }
            }
            Files.walkFileTree(handlerScriptDir, simpleFileVisitor)

            handlerWatchThread =
                Thread(
                    HandlerWatch(watchService, watchKeyMap, handlerFileInfoMap, fileHandlerMap, handlerScriptDir),
                    "HandlerWatchThread"
                )
            handlerWatchThread!!.isDaemon = true
            handlerWatchThread!!.start()

            return fileHandlerMap.map { it.value }
        }
    }

    private inner class HandlerWatch(
        val watchService: WatchService,
        val watchKeyMap: MutableMap<WatchKey, Path>,
        val handlerFileMap: MutableMap<String, HandlerFileInfo>,
        val fileHandlerMap: MutableMap<Path, TelegramHandler>,
        val handlerScriptDir: Path
    ) : Runnable {

        override fun run() {
            watchService.use { watchService ->
                var watchKey: WatchKey
                while (watchService.take().also { watchKey = it } != null) {
                    try {
                        val dir = watchKeyMap[watchKey]
                        if (dir == null) {
                            logger.error { "Can't resolve watchKey" }
                            continue
                        }
                        for (event in watchKey.pollEvents()) {
                            val path = handlerScriptDir.resolve(event.context() as Path)
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
                                changeListenerList.forEach { it.invoke(oldHandler, newHandler) }
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
