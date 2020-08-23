package org.botlaxy.telegramit.core.handler.dsl

import org.botlaxy.telegramit.core.handler.HandlerCommand

class ConversationTelegramHandler(
    val commands: List<HandlerCommand>,
    private val steps: Map<String, Step<*>>,
    val process: ProcessBlock
) : TelegramHandler {

    fun getStep(key: String): Step<*>? {
        return steps[key]
    }

    fun getFirstStep(): Step<*>? {
        return if (steps.entries.isNotEmpty()) {
            steps.entries.first().value
        } else {
            null
        }
    }

    override fun type(): TelegramHandlerType {
        return TelegramHandlerType.CONVERSATION
    }

    override fun toString(): String {
        return "Handler(commands=$commands)"
    }

}
