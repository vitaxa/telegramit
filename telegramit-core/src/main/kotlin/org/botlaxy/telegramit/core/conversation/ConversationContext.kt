package org.botlaxy.telegramit.core.conversation

import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.client.model.inline.CallbackQuery
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class ConversationContext(
    val telegram: TelegramApi,
    private val telegramMessage: AtomicReference<TelegramMessage>,
    private val callbackQuery: AtomicReference<CallbackQuery>,
    val answer: MutableMap<String, Any> = ConcurrentHashMap(),
    val store: MutableMap<String, Any> = ConcurrentHashMap()
) {

    var message: TelegramMessage
        get() = telegramMessage.get()
        set(value) {
            telegramMessage.set(value)
        }

    var callback: CallbackQuery?
        get() = callbackQuery.get()
        set(value) {
            callbackQuery.set(value)
        }

}
