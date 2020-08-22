package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultVenue(
    id: String,
    @JsonProperty("latitude")
    val latitude: Float,
    @JsonProperty("longitude")
    val longitude: Float,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("foursquare_id")
    val foursquareId: String? = null,
    @JsonProperty("foursquare_type")
    val foursquareType: String? = null,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup? = null,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent? = null,
    @JsonProperty("thumb_url")
    val thumbUrl: String? = null,
    @JsonProperty("thumb_width")
    val thumbWidth: Int? = null,
    @JsonProperty("thumb_height")
    val thumbHeight: Int? = null
) : InlineQueryResult("venue", id)
