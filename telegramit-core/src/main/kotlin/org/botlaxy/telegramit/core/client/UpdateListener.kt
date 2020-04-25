package org.botlaxy.telegramit.core.client

import org.botlaxy.telegramit.core.client.model.TelegramUpdate

interface UpdateListener {
    fun onUpdate(update: TelegramUpdate)
}
