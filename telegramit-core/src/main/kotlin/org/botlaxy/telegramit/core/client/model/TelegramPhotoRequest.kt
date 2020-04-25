package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

open class TelegramPhotoRequest(
    chatId: Long,
    @get:JsonProperty("photo")
    val photo: ByteArray,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
