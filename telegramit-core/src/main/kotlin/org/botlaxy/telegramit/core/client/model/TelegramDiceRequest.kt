package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramDiceRequest(
    chatId: Long,
    @get:JsonProperty("emoji")
    var emoji: String,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false
) : TelegramRequest(chatId, replyKeyboard, disableNotification) {

}
