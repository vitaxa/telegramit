package org.botlaxy.telegramit.core.listener

import org.botlaxy.telegramit.core.client.UpdateListener
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.extension.getEditMessage
import org.botlaxy.telegramit.core.extension.getMessage
import org.botlaxy.telegramit.core.extension.isInlineMessage
import org.botlaxy.telegramit.core.handler.filter.DefaultTelegramUpdateFilterChain
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilterChain

class FilterUpdateListener(
    private val updateFilters: List<TelegramUpdateFilter>
) : UpdateListener {

    override fun onUpdate(update: TelegramUpdate) {
        var filterChain: TelegramUpdateFilterChain? = null
        if (update.getMessage() != null || update.getEditMessage() != null) {
            filterChain = DefaultTelegramUpdateFilterChain(updateFilters)
        }
        if (update.isInlineMessage()) {
            filterChain = DefaultTelegramUpdateFilterChain(updateFilters)
        }
        filterChain?.doFilter(update)
    }
}
