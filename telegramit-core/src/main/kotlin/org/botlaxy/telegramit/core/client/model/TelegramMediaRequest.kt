package org.botlaxy.telegramit.core.client.model

open class TelegramMediaRequest(
    chatId: Long,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    val caption: String? = null,
    val parseMode: TelegramParseMode? = null
) : TelegramRequest(chatId, replyKeyboard, disableNotification)
