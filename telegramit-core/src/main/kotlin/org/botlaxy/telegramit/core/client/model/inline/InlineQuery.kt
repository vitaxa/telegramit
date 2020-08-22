package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.TelegramLocation
import org.botlaxy.telegramit.core.client.model.TelegramUser

data class InlineQuery(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("from")
    val from: TelegramUser,
    @JsonProperty("location")
    val location: TelegramLocation?,
    @JsonProperty("query")
    val query: String,
    @JsonProperty("offset")
    val offset: String
)

fun InlineQuery.toHandlerQuery(): InlineHandlerQuery {
    return InlineHandlerQuery(query, from, offset, location)
}
