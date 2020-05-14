package org.botlaxy.telegramit.core.handler.loader.collect

import mu.KotlinLogging
import org.botlaxy.telegramit.core.extension.getFileSystem
import org.botlaxy.telegramit.core.handler.HandlerConstant
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.HashSet

private val logger = KotlinLogging.logger {}

class ClassPathScriptCollector() : ScriptCollector {

    override fun collect(): List<Path> {
        logger.debug { "Search scripts at the '${HandlerConstant.HANDLERS_DIR}' resource directory" }
        val resourceHandlers: MutableSet<URL> = findAllInResource(HandlerConstant.HANDLERS_DIR)
        val scriptFiles = mutableListOf<Path>()
        for (resourceHandler in resourceHandlers) {
            val resourceHandlerUri = resourceHandler.toURI()
            if (resourceHandlerUri.scheme == "file") {
                File(resourceHandler.path).walkTopDown().forEach {
                    val filePath = it.toPath()
                    val fileName = filePath.fileName.toString()
                    if (fileName.endsWith(HandlerConstant.KOTLIN_SCRIPT_EXT)) {
                        scriptFiles.add(filePath)
                    }
                }
            } else {
                resourceHandlerUri.getFileSystem().use { fileSystem ->
                    Files.walk(fileSystem.getPath(HandlerConstant.HANDLERS_DIR)).forEach { filePath ->
                        val fileName = filePath.fileName.toString()
                        if (fileName.endsWith(HandlerConstant.KOTLIN_SCRIPT_EXT)) {
                            scriptFiles.add(filePath)
                        }
                    }
                }
            }
        }
        logger.debug {
            val handlers = scriptFiles.joinToString { it.fileName.toString() }
            "Classpath handlers: $handlers"
        }
        return scriptFiles
    }

    private fun findAllInResource(location: String): MutableSet<URL> {
        val resultResourceUrls: MutableSet<URL> = HashSet()
        val resourceUrls: Enumeration<URL> = ClassLoader.getSystemResources(location)
        while (resourceUrls.hasMoreElements()) {
            val url = resourceUrls.nextElement()
            resultResourceUrls.add(url)
        }
        logger.debug { "Resource path: $resultResourceUrls" }
        return resultResourceUrls
    }

}
