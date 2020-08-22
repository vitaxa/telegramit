package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.TelegramReplyKeyboard

class TelegramInlineReplyKeyboardMarkup(
    keyboardRows: List<InlineKeyboardButton>
) : TelegramReplyKeyboard {

    @JsonProperty("inline_keyboard")
    private val inlineKeyboard: List<List<InlineKeyboardButton>> = keyboardRows.asSequence()
        .chunked(COLUMN_NUM)
        .toList()

    companion object {
        private const val COLUMN_NUM = 2
    }
}
