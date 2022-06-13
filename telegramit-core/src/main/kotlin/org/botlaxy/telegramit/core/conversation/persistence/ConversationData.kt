package org.botlaxy.telegramit.core.conversation.persistence

import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.client.model.inline.CallbackQuery

data class ConversationData(
    val handlerCommand: String,
    val stepKey: String,
    val message: TelegramMessage,
    val callbackQuery: CallbackQuery?,
    val answer: Map<String, Any>,
    val store: Map<String, Any>,
    val stage: String
)
