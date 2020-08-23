package org.botlaxy.telegramit.core.handler.filter

import org.botlaxy.telegramit.core.client.model.TelegramUpdate

class DefaultTelegramUpdateFilterChain(
    updateFilters: List<TelegramUpdateFilter>
) : TelegramUpdateFilterChain {

    private val iterator: Iterator<TelegramUpdateFilter> = updateFilters.iterator()

    override fun doFilter(update: TelegramUpdate) {
        if (iterator.hasNext()) {
            val filter = iterator.next()
            filter.handleUpdate(update, this)
        }
    }
}
