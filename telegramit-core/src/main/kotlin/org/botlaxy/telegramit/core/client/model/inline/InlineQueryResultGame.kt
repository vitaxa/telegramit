package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultGame(
    id: String,
    @JsonProperty("game_short_name")
    val gameShortName: String,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup? = null
) : InlineQueryResult("game", id)
