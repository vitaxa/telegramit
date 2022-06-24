package org.botlaxy.telegramit.core.listener

import org.botlaxy.telegramit.core.client.UpdateListener
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.extension.getEditMessage
import org.botlaxy.telegramit.core.extension.getMessage
import org.botlaxy.telegramit.core.extension.isInlineMessage
import org.botlaxy.telegramit.core.handler.filter.DefaultTelegramUpdateFilterChain
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilterChain

class FilterUpdateListener(
    private val updateFilters: List<TelegramUpdateFilter>,
    private val conversationManager: ConversationManager
) : UpdateListener {

    override fun onUpdate(update: TelegramUpdate) {
        var filterChain: TelegramUpdateFilterChain? = null
        if (update.getMessage() != null || update.getEditMessage() != null) {
            filterChain = DefaultTelegramUpdateFilterChain(updateFilters, conversationManager)
        }
        if (update.isInlineMessage()) {
            filterChain = DefaultTelegramUpdateFilterChain(updateFilters, conversationManager)
        }
        if (update.callbackQuery != null) {
            filterChain = DefaultTelegramUpdateFilterChain(updateFilters, conversationManager)
        }
        filterChain?.doFilter(update)
    }
}
