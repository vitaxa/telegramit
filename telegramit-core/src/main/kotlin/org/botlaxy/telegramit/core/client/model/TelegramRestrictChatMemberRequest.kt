package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramRestrictChatMemberRequest(
    @get:JsonProperty("chat_id")
    val chatId: Long,
    @get:JsonProperty("user_id")
    val userId: Long,
    @get:JsonProperty("permissions")
    val permissions: TelegramChatPermissions,
    @get:JsonProperty("until_date")
    val untilDate: Int
)
