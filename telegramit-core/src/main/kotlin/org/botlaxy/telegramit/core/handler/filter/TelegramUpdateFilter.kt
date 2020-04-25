package org.botlaxy.telegramit.core.handler.filter

import org.botlaxy.telegramit.core.client.model.TelegramUpdate

interface TelegramUpdateFilter {
    fun handleUpdate(
        update: TelegramUpdate,
        filterChain: TelegramUpdateFilterChain
    )
}
