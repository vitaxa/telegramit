package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultDocument(
    id: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("caption")
    val caption: String?,
    @JsonProperty("parse_mode")
    val parseMode: String?,
    @JsonProperty("document_url")
    val documentUrl: String,
    @JsonProperty("mime_type")
    val mimeType: String,
    @JsonProperty("description")
    val description: String? = null,
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
) : InlineQueryResult("document", id)
