package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramContact(
    @JsonProperty("phone_number")
    val phoneNumber: String,

    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String,

    @JsonProperty("user_id")
    val userId: Long,

    @JsonProperty("vcard")
    val vcard: String


)
