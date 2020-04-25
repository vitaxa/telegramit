package org.botlaxy.telegramit.core.conversation

import org.botlaxy.telegramit.core.handler.HandlerCommand
import org.botlaxy.telegramit.core.handler.dsl.Handler
import org.botlaxy.telegramit.core.handler.dsl.Step

class ConversationState(
    val handlerCommand: HandlerCommand,
    val handler: Handler,
    var ctx: ConversationContext
) {
    var currentStep: Step<*>? = handler.getFirstStep()
}
