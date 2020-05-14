package org.botlaxy.telegramit.core.client.model

open class TelegramAudioRequest(
    chatId: Long,
    val audio: AudioRequest,
    val duration: Int? = null,
    val performer: String? = null,
    val title: String? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
