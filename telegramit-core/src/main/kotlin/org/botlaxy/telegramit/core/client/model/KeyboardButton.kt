package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class KeyboardButton(
    @JsonProperty("text")
    val text: String,
    @JsonProperty("request_contact")
    val requestContact: Boolean = false,
    @JsonProperty("request_location")
    val requestLocation: Boolean = false
)
