package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramReplyKeyboardMarkup(
    keyboardRows: List<KeyboardButton>,
    @get:JsonProperty("resize_keyboard")
    val resizeKeyboard: Boolean = true,
    @get:JsonProperty("one_time_keyboard")
    val oneTimeKeyboard: Boolean = true,
    @get:JsonProperty("selective")
    val selective: Boolean = false
) : TelegramReplyKeyboard {

    @JsonProperty("keyboard")
    private val keyboard: List<List<KeyboardButton>> = keyboardRows.asSequence()
        .chunked(COLUMN_NUM)
        .toList()

    companion object {
        private const val COLUMN_NUM = 2
    }

}
