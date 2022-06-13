package org.botlaxy.telegramit.core.conversation

import org.botlaxy.telegramit.core.client.api.TelegramApi

interface ConversationStateSubscriber {
    fun update(conversationState: ConversationState?, telegramApi: TelegramApi)
}
