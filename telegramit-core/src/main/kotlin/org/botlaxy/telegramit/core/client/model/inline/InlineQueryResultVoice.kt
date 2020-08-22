package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultVoice(
    id: String,
    @JsonProperty("voice_url")
    val voiceUrl: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("caption")
    val caption: String? = null,
    @JsonProperty("parse_mode")
    val parseMode: String? = null,
    @JsonProperty("voice_duration")
    val voiceDuration: Int? = null,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup? = null,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent? = null
) : InlineQueryResult("voice", id)
