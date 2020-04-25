package org.botlaxy.telegramit.core.client

import org.botlaxy.telegramit.core.client.model.TelegramUpdate

interface TelegramClient {

    fun start()

    fun onUpdate(update: TelegramUpdate)

    fun close()

}
