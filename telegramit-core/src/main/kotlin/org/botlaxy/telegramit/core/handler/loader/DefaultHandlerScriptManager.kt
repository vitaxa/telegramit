package org.botlaxy.telegramit.core.handler.loader

import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollectorException
import org.botlaxy.telegramit.core.handler.loader.compile.HandlerScriptCompiler

class DefaultHandlerScriptManager(
    private val handlerScriptCompiler: HandlerScriptCompiler,
    private val scriptCollector: ScriptCollector
) : HandlerScriptManager {

    override fun compileScripts(): List<TelegramHandler> {
        try {
            val handlerScriptFiles = scriptCollector.collect()
            return handlerScriptFiles.map { handlerScriptCompiler.compile(it) }
        } catch (e: ScriptCollectorException) {
            throw IllegalStateException("Exception during collecting scripts", e)
        }
    }

}
