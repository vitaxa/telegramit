package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramUser(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("is_bot")
    val isBot: Boolean,

    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String?,

    @JsonProperty("username")
    val username: String?,

    @JsonProperty("language_code")
    val languageCode: String?,

    @JsonProperty("can_join_groups")
    val canJoinGroups: Boolean?,

    @JsonProperty("can_read_all_group_messages")
    val canReadAllGroupMessages: Boolean?
)
