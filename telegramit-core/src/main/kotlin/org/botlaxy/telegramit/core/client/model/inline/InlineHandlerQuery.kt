package org.botlaxy.telegramit.core.client.model.inline

import org.botlaxy.telegramit.core.client.model.TelegramLocation
import org.botlaxy.telegramit.core.client.model.TelegramUser

data class InlineHandlerQuery(
    val query: String,
    val from: TelegramUser,
    val offset: String,
    val location: TelegramLocation?
)
