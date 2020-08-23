package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultGif(
    id: String,
    @JsonProperty("gif_url")
    val gifUrl: String,
    @JsonProperty("gif_width")
    val gifWidth: Int? = null,
    @JsonProperty("gif_height")
    val gifHeight: Int? = null,
    @JsonProperty("gif_duration")
    val gifDuration: Int? = null,
    @JsonProperty("thumb_url")
    val thumbUrl: String,
    @JsonProperty("thumb_mime_type")
    val thumbMimeType: String? = null,
    @JsonProperty("title")
    val title: String? = null,
    @JsonProperty("caption")
    val caption: String? = null,
    @JsonProperty("parse_mode")
    val parseMode: String? = null,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup? = null,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent? = null
) : InlineQueryResult("gif", id)
