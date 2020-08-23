package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.TelegramReplyKeyboardMarkup

class InlineQueryResultLocation(
    id: String,
    @JsonProperty("latitude")
    val latitude: Float,
    @JsonProperty("longitude")
    val longitude: Float,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("live_period")
    val livePeriod: Int? = null,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramReplyKeyboardMarkup? = null,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent? = null,
    @JsonProperty("thumb_url")
    val thumbUrl: String? = null,
    @JsonProperty("thumb_width")
    val thumbWidth: Int? = null,
    @JsonProperty("thumb_height")
    val thumbHeight: Int? = null
) : InlineQueryResult("location", id)
