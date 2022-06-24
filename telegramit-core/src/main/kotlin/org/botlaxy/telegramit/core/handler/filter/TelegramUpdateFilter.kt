package org.botlaxy.telegramit.core.handler.filter

import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.conversation.ConversationManager

interface TelegramUpdateFilter {
    fun handleUpdate(
        update: TelegramUpdate,
        conversationManager: ConversationManager,
        filterChain: TelegramUpdateFilterChain
    )
}
