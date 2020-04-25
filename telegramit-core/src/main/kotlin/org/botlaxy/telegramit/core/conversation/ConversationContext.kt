package org.botlaxy.telegramit.core.conversation

import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class ConversationContext(
    val telegram: TelegramApi,
    private val telegramMessage: AtomicReference<TelegramMessage>,
    val answer: MutableMap<String, Any> = ConcurrentHashMap(),
    val store: MutableMap<String, Any> = ConcurrentHashMap()
) {

    var message: TelegramMessage
        get() = telegramMessage.get()
        set(value) {
            telegramMessage.set(value)
        }

}
