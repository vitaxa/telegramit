package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultAudio(
    id: String,
    @JsonProperty("audio_url")
    val audioUrl: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("caption")
    val caption: String? = null,
    @JsonProperty("parse_mode")
    val parseMode: String? = null,
    @JsonProperty("performer")
    val performer: String? = null,
    @JsonProperty("audio_duration")
    val audioDuration: Int? = null,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup? = null,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent? = null
) : InlineQueryResult("audio", id)
