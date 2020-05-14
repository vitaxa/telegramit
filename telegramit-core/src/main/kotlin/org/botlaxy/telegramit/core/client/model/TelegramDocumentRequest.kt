package org.botlaxy.telegramit.core.client.model

open class TelegramDocumentRequest(
    chatId: Long,
    val document: DocumentRequest,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramMediaRequest(chatId, replyKeyboard, disableNotification, caption, parseMode)
