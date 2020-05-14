package org.botlaxy.telegramit.core.client.model

open class TelegramPhotoRequest(
    chatId: Long,
    val photo: PhotoRequest,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
