package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramReplyKeyboardMarkup(
    @get:JsonProperty("keyboard")
    val keyboard: List<List<KeyboardButton>>,
    @get:JsonProperty("resize_keyboard")
    val resizeKeyboard: Boolean = true,
    @get:JsonProperty("one_time_keyboard")
    val oneTimeKeyboard: Boolean = true,
    @get:JsonProperty("selective")
    val selective: Boolean = false
) : TelegramReplyKeyboard
