package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.TelegramLocation
import org.botlaxy.telegramit.core.client.model.TelegramUser

data class ChosenInlineQuery(
    @JsonProperty("result_id")
    val resultId: String,
    @JsonProperty("from")
    val from: TelegramUser,
    @JsonProperty("location")
    val location: TelegramLocation?,
    @JsonProperty("inline_message_id")
    val inlineMessageId: String?,
    @JsonProperty("query")
    val query: String
)
