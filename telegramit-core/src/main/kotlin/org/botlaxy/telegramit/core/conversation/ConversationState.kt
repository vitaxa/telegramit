package org.botlaxy.telegramit.core.conversation

import org.botlaxy.telegramit.core.handler.HandlerCommand
import org.botlaxy.telegramit.core.handler.dsl.StepTelegramHandler
import org.botlaxy.telegramit.core.handler.dsl.Step

class ConversationState(
    val handlerCommand: HandlerCommand,
    val handler: StepTelegramHandler,
    var ctx: ConversationContext
) {
    var currentStep: Step<*>? = handler.getFirstStep()
}
