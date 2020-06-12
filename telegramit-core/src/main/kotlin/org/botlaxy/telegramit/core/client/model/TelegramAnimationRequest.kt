package org.botlaxy.telegramit.core.client.model

open class TelegramAnimationRequest(
    chatId: Long,
    val animation: AnimationRequest,
    val duration: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
