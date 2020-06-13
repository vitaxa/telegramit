package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramBotCommand(
    @get:JsonProperty("command")
    val command: String,
    @get:JsonProperty("description")
    val description: String
)
