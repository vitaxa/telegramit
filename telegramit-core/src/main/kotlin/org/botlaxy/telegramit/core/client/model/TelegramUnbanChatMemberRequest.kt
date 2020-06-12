package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramUnbanChatMemberRequest(
    @get:JsonProperty("chat_id")
    val chatId: Long,
    @get:JsonProperty("user_id")
    val userId: Long
)
