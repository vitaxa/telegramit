package org.botlaxy.telegramit.core.handler.loader.collect

import mu.KotlinLogging
import org.botlaxy.telegramit.core.handler.HandlerConstant
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

class DirectoryScriptCollector(val location: String) : ScriptCollector {

    override fun collect(): List<Path> {
        logger.debug { "Search scripts at the '$location' directory" }
        val handlerPath = Paths.get(location)

        if (!Files.isDirectory(handlerPath)) {
            throw ScriptCollectorException("Bad handler scripts location: $location")
        }

        val scriptFiles = mutableListOf<Path>()
        handlerPath.toFile().walk(FileWalkDirection.TOP_DOWN)
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
