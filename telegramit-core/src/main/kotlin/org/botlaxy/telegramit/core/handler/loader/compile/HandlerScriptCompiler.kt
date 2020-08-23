package org.botlaxy.telegramit.core.handler.loader.compile

import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import java.nio.file.Path

interface HandlerScriptCompiler {
    fun compile(file: Path): TelegramHandler
}
