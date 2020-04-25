package org.botlaxy.telegramit.core.handler.filter

import org.botlaxy.telegramit.core.client.model.TelegramUpdate

interface TelegramUpdateFilterChain {
    fun doFilter(update: TelegramUpdate)
}
