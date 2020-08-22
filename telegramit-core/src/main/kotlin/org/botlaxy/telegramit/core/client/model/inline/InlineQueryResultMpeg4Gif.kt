package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.TelegramReplyKeyboardMarkup

class InlineQueryResultMpeg4Gif(
    id: String,
    @JsonProperty("mpeg4_url")
    val mpeg4Url: String,
    @JsonProperty("mpeg4_width")
    val mpeg4Width: Int? = null,
    @JsonProperty("mpeg4_height")
    val mpeg4Height: Int? = null,
    @JsonProperty("mpeg4_duration")
    val mpeg4Duration: Int? = null,
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
    val replyMarkup: TelegramReplyKeyboardMarkup,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent
) : InlineQueryResult("mpeg4_gif", id)
