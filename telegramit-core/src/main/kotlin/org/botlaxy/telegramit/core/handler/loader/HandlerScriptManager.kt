package org.botlaxy.telegramit.core.handler.loader

import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler

interface HandlerScriptManager {

    fun compileScripts(): List<TelegramHandler>

}
