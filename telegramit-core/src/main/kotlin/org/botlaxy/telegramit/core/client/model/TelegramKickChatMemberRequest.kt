package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramKickChatMemberRequest(
    @get:JsonProperty("chat_id")
    val chatId: Long,
    @get:JsonProperty("user_id")
    val userId: Long,
    @get:JsonProperty("until_date")
    val untilDate: Int
)
