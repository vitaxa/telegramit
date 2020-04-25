package org.botlaxy.telegramit.core.conversation.persistence

import org.botlaxy.telegramit.core.client.model.TelegramMessage

data class ConversationData(
    val handlerCommand: String,
    val stepKey: String,
    val message: TelegramMessage,
    val answer: Map<String, Any>,
    val store: Map<String, Any>
)
