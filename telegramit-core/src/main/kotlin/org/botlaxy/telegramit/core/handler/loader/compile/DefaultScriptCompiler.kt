package org.botlaxy.telegramit.core.handler.loader.compile

import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import java.io.BufferedReader
import javax.script.ScriptEngineFactory

class DefaultScriptCompiler(
    private val scriptEngineFactory: ScriptEngineFactory
) : AbstractHandlerScriptCompiler() {

    override fun scriptEval(bufferedReader: BufferedReader): TelegramHandler {
        return scriptEngineFactory.scriptEngine.eval(bufferedReader) as TelegramHandler
    }

}
