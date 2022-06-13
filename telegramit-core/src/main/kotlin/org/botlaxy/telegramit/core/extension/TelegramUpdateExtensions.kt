package org.botlaxy.telegramit.core.extension

import io.ktor.http.Headers
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
        callbackQuery?.message?.chat?.id != null -> {
            callbackQuery.message.chat.id
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
        }
        else -> null
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

fun TelegramUpdate.isInlineMessage(): Boolean {
    return inlineQuery != null || chosenInlineResult != null
}

operator fun Headers.plus(other: Headers): Headers = when {
    this.isEmpty() -> other
    other.isEmpty() -> this
    else -> Headers.build {
        appendAll(this@plus)
        appendAll(other)
    }
}
