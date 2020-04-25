package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

open class TelegramAudioRequest(
    chatId: Long,
    @get:JsonProperty("audio")
    val audio: AudioRequest,
    @get:JsonProperty("duration")
    val duration: Int? = null,
    @get:JsonProperty("performer")
    val performer: String? = null,
    @get:JsonProperty("title")
    val title: String? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
