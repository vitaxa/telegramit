package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChatPermissionRequest(
    @get:JsonProperty("chat_id")
    val chatId: Long,
    @get:JsonProperty("permissions")
    val permissions: TelegramChatPermissions
)
