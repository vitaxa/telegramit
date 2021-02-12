package org.botlaxy.telegramit.core.handler.loader.compile

import mu.KotlinLogging
import org.botlaxy.telegramit.core.extension.getFileSystem
import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import java.io.BufferedReader
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

abstract class AbstractHandlerScriptCompiler : HandlerScriptCompiler {

    override fun compile(file: Path): TelegramHandler {
        return try {
            logger.debug { "Compile $file" }
            val fileUri = file.toUri()
            if (fileUri.scheme == "file") {
                Files.newInputStream(file).bufferedReader().use {
                    scriptEval(it)
                }
            } else {
                fileUri.getFileSystem().use { fileSystem: FileSystem ->
                    Files.newInputStream(fileSystem.getPath(file.toString())).bufferedReader().use {
                        scriptEval(it)
                    }
                }
            }
        } catch (e: Exception) {
            throw HandlerCompileException("Can't compile ${file.fileName}", e)
        }
    }

    abstract fun scriptEval(bufferedReader: BufferedReader): TelegramHandler

}
