package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramInlineKeyboardMarkup(
    @JsonProperty("inline_keyboard")
    val keyboardRows: List<List<InlineKeyboardButton>>
) : TelegramReplyKeyboard
