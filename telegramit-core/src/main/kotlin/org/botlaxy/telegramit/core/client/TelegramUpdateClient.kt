package org.botlaxy.telegramit.core.client

import org.botlaxy.telegramit.core.client.model.TelegramUpdate

interface TelegramUpdateClient {

    fun start()

    fun onUpdate(update: TelegramUpdate)

    fun close()

}
