package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramContactRequest(
    chatId: Long,
    @get:JsonProperty("phone_number")
    val phoneNumber: String,
    @get:JsonProperty("first_name")
    val firstName: String,
    @get:JsonProperty("last_name")
    var lastName: String,
    @get:JsonProperty("vcard")
    var vcard: String,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false
) : TelegramRequest(chatId, replyKeyboard, disableNotification)
