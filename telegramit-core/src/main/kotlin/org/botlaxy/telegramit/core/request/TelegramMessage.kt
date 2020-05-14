package org.botlaxy.telegramit.core.request

import org.botlaxy.telegramit.core.client.model.*

class AudioMessage(
    audio: AudioRequest,
    chatId: Long = 0L,
    duration: Int? = null,
    performer: String? = null,
    title: String? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramAudioRequest(
    chatId,
    audio,
    duration,
    performer,
    title,
    replyKeyboard,
    disableNotification,
    caption,
    parseMode
)

class DocumentMessage(
    document: DocumentRequest,
    chatId: Long = 0L,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramDocumentRequest(chatId, document, replyKeyboard, disableNotification, caption, parseMode)


class TextMessage(
    text: String,
    chatId: Long = 0L,
    parseMode: TelegramParseMode? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableWebPagePreview: Boolean = false,
    disableNotification: Boolean = false
) : TelegramChatRequest(chatId, text, parseMode, replyKeyboard, disableWebPagePreview, disableNotification)

class PhotoMessage(
    photo: PhotoRequest,
    chatId: Long = 0L,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramPhotoRequest(chatId, photo, replyKeyboard, disableNotification, caption, parseMode)


class VideoMessage(
    video: VideoRequest,
    chatId: Long = 0L,
    duration: Int? = null,
    width: Int? = null,
    height: Int? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false,
    caption: String? = null,
    parseMode: TelegramParseMode? = null
) : TelegramVideoRequest(chatId, video, duration, width, height, replyKeyboard, disableNotification, caption, parseMode)
