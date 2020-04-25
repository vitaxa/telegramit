package org.botlaxy.telegramit.core.client.model

open class TelegramVideoRequest(
    chatId: Long,
    val video: VideoRequest,
    val duration: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
