package org.botlaxy.telegramit.core.conversation

import org.botlaxy.telegramit.core.handler.HandlerCommand
import org.botlaxy.telegramit.core.handler.dsl.ConversationTelegramHandler
import org.botlaxy.telegramit.core.handler.dsl.Step

class ConversationState(
    val handlerCommand: HandlerCommand,
    val handler: ConversationTelegramHandler,
    var ctx: ConversationContext
) {
    var currentStep: Step<*>? = handler.getFirstStep()
}
