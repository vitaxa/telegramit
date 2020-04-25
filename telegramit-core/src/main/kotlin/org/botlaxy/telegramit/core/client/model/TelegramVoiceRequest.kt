package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramVoiceRequest(
    chatId: Long,
    @get:JsonProperty("voice")
    val voice: VoiceRequest,
    @get:JsonProperty("duration")
    val duration: Int? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
