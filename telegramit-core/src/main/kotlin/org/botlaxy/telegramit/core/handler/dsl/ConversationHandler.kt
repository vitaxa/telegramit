package org.botlaxy.telegramit.core.handler.dsl

import org.botlaxy.telegramit.core.handler.HandlerCommand

class ConversationHandler(
    val commands: List<HandlerCommand>,
    private val steps: Map<String, Step<*>>,
    val process: ProcessBlock
) : Handler {

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

    override fun type(): HandlerType {
        return HandlerType.CONVERSATION
    }

    override fun toString(): String {
        return "Handler(commands=$commands)"
    }

}
