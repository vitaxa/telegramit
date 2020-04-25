package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChat(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("type")
    val type: String,

    @JsonProperty("title")
    val title: String?,

    @JsonProperty("username")
    val username: String?,

    @JsonProperty("first_name")
    val firstName: String?,

    @JsonProperty("last_name")
    val lastName: String?
)
