package org.botlaxy.telegramit.core.handler.loader.collect

import mu.KotlinLogging
import org.botlaxy.telegramit.core.handler.HandlerConstant
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

class DirectoryScriptCollector(val location: String) : ScriptCollector {

    override fun collect(): List<Path> {
        logger.debug { "Search scripts at the '$location' working directory" }
        val workDir = Paths.get(System.getProperty("user.dir"))
        val handlersDir = workDir.resolve(HandlerConstant.HANDLERS_DIR)
        val scriptFiles = mutableListOf<Path>()
        handlersDir.toFile().walk(FileWalkDirection.TOP_DOWN)
            .forEach {
                val filePath = it.toPath()
                val fileName = filePath.fileName.toString()
                if (fileName.endsWith(HandlerConstant.KOTLIN_SCRIPT_EXT)) {
                    scriptFiles.add(filePath)
                }
            }
        logger.debug {
            val handlers = scriptFiles.joinToString { it.fileName.toString() }
            "WorkDir handlers: $handlers"
        }

        return scriptFiles
    }

}
