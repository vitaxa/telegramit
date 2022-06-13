package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramReplyKeyboardRemove(
    @get:JsonProperty("remove_keyboard")
    val removeKeyboard: Boolean = true,
    @get:JsonProperty("selective")
    val selective: Boolean = false
) : TelegramReplyKeyboard
