package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramLocation(
    @JsonProperty("longitude")
    val longitude: Float,
    @JsonProperty("latitude")
    val latitude: Float
)
