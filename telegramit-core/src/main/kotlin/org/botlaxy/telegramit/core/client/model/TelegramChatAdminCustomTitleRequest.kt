package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChatAdminCustomTitleRequest(
    @get:JsonProperty("chat_id")
    val chatId: Long,
    @get:JsonProperty("user_id")
    val userId: Long,
    @get:JsonProperty("custom_title")
    var customTile: String
)
