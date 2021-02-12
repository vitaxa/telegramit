package org.botlaxy.telegramit.spring.handler.loader.compile

import mu.KotlinLogging
import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import org.botlaxy.telegramit.core.handler.loader.compile.AbstractHandlerScriptCompiler
import org.botlaxy.telegramit.spring.handler.dsl.SpringHandlerDslWrapper
import org.botlaxy.telegramit.spring.handler.dsl.SpringInlineHandlerDslWrapper
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import org.springframework.context.support.GenericApplicationContext
import java.io.BufferedReader

private val logger = KotlinLogging.logger {}

class SpringScriptCompiler(
    private val context: GenericApplicationContext
) : AbstractHandlerScriptCompiler() {

    private val scriptEngineFactory = KotlinJsr223JvmLocalScriptEngineFactory()

    override fun scriptEval(bufferedReader: BufferedReader): TelegramHandler {
        val compiledScript = scriptEngineFactory.scriptEngine.eval(bufferedReader)
        return when {
            checkScriptType<SpringHandlerDslWrapper>(compiledScript) -> {
                @Suppress("UNCHECKED_CAST")
                (compiledScript as SpringHandlerDslWrapper)(context)
            }
            checkScriptType<SpringInlineHandlerDslWrapper>(compiledScript) -> {
                @Suppress("UNCHECKED_CAST")
                (compiledScript as SpringInlineHandlerDslWrapper)(context)
            }
            else -> {
                throw IllegalStateException("Bad handler type")
            }
        }
    }

    private inline fun <reified T> checkScriptType(compiledScript: Any): Boolean {
        return compiledScript is T
    }

}
