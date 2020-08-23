package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultArticle(
    id: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent,
    @JsonProperty("reply_markup")
    val replyKeyboardMarkup: TelegramInlineReplyKeyboardMarkup? = null,
    @JsonProperty("url")
    val url: String? = null,
    @JsonProperty("hide_url")
    val hideUrl: Boolean = false,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("thumb_url")
    val thumbUrl: String? = null,
    @JsonProperty("thumb_width")
    val thumbWidth: Int? = null,
    @JsonProperty("thumb_height")
    val thumbHeight: Int? = null
) : InlineQueryResult("article", id)
