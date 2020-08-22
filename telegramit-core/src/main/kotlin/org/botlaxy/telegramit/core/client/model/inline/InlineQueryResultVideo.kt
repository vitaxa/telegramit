package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultVideo(
    id: String,
    @JsonProperty("video_url")
    val videoUrl: String,
    @JsonProperty("mime_type")
    val mimeType: String,
    @JsonProperty("thumb_url")
    val thumbUrl: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("caption")
    val caption: String? = null,
    @JsonProperty("parse_mode")
    val parseMode: String? = null,
    @JsonProperty("video_width")
    val videoWidth: Int? = null,
    @JsonProperty("video_height")
    val videoHeight: Int? = null,
    @JsonProperty("video_duration")
    val videoDuration: Int? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup? = null,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent? = null
) : InlineQueryResult("video", id)
