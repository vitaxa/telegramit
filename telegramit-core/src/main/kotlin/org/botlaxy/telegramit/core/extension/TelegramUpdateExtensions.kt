package org.botlaxy.telegramit.core.extension

import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.client.model.TelegramUpdate

fun TelegramUpdate.getChatId(): Long {
    return when {
        message?.chat?.id != null -> {
            message.chat.id
        }
        editedMessage?.chat?.id != null -> {
            editedMessage.chat.id
        }
        channelPost?.chat?.id != null -> {
            channelPost.chat.id
        }
        editedChannelPost?.chat?.id != null -> {
            editedChannelPost.chat.id
        }
        else -> throw IllegalStateException("chatId can't be null")
    }
}

fun TelegramUpdate.getMessage(): TelegramMessage? {
    return when {
        message != null -> {
            message
        }
        channelPost != null -> {
            channelPost
        } else -> null
    }
}

fun TelegramUpdate.getEditMessage(): TelegramMessage? {
    return when {
        editedMessage != null -> {
            editedMessage
        }
        editedChannelPost != null -> {
            editedChannelPost
        }
        else -> null
    }
}
