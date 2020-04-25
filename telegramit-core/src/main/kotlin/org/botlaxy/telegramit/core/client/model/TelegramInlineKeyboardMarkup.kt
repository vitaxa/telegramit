package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramInlineKeyboardMarkup(keyboardRows: List<InlineKeyboardButton>) : TelegramReplyKeyboard {

    @JsonProperty("inline_keyboard")
    private val keyboard: List<List<InlineKeyboardButton>> = keyboardRows.asSequence()
        .chunked(COLUMN_NUM)
        .toList()

    companion object {
        private const val COLUMN_NUM = 2
    }
}
