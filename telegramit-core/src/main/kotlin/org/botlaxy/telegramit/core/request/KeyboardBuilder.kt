package org.botlaxy.telegramit.core.request

import org.botlaxy.telegramit.core.client.model.*

@DslMarker
annotation class KeyboardDsl

fun keyboard(body: KeyboardBuilder.() -> Unit): TelegramReplyKeyboardMarkup {
    return KeyboardBuilder().build(body)
}

fun inlineKeyboard(body: InlineKeyboardBuilder.() -> Unit): TelegramInlineKeyboardMarkup {
    return InlineKeyboardBuilder().build(body)
}

@KeyboardDsl
class KeyboardBuilder {
    private var row = arrayListOf<KeyboardButton>()

    fun row(body: KeyboardButtonBuilder.() -> Unit) {
        val keyboardButtonBuilder = KeyboardButtonBuilder().apply(body)
        row.addAll(keyboardButtonBuilder)
    }

    fun build(body: KeyboardBuilder.() -> Unit): TelegramReplyKeyboardMarkup {
        body()
        return TelegramReplyKeyboardMarkup(row)
    }
}

@KeyboardDsl
class KeyboardButtonBuilder : ArrayList<KeyboardButton>() {

    var requestContact: Boolean = false

    var requestLocation: Boolean = false

    fun button(text: String, body: (KeyboardButtonBuilder.() -> Unit)? = null) {
        if (body != null) {
            body()
        }
        add(KeyboardButton(text, requestContact, requestLocation))
    }
}

@KeyboardDsl
class InlineKeyboardBuilder {
    private val row = arrayListOf<InlineKeyboardButton>()

    fun row(body: InlineKeyboardButtonBuilder.() -> Unit) {
        val inlineKeyboardButtonBuilder = InlineKeyboardButtonBuilder().apply(body)
        row.addAll(inlineKeyboardButtonBuilder)
    }

    fun build(body: InlineKeyboardBuilder.() -> Unit): TelegramInlineKeyboardMarkup {
        body()
        return TelegramInlineKeyboardMarkup(row)
    }

}

@KeyboardDsl
class InlineKeyboardButtonBuilder : ArrayList<InlineKeyboardButton>() {

    var url: String? = null

    var loginUrl: LoginUrl? = null

    var callbackData: String? = null

    var switchInlineQuery: String? = null

    var switchInlineQueryCurrentChat: String? = null

    var pay: Boolean = false

    fun button(text: String, body: (InlineKeyboardButtonBuilder.() -> Unit)? = null) {
        if (body != null) {
            body()
        }
        val keyboardButton = InlineKeyboardButton(
            text,
            url,
            loginUrl,
            callbackData,
            switchInlineQuery,
            switchInlineQueryCurrentChat,
            pay
        )
        add(keyboardButton)
    }

}
