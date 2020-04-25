package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
open class TelegramChatRequest(
    chatId: Long,
    @get:JsonProperty("text")
    val text: String,
    @get:JsonProperty("parse_mode")
    val parseMode: TelegramParseMode? = null,
    replyKeyboard: TelegramReplyKeyboard? = null,
    @get:JsonProperty("disable_web_page_preview")
    val disableWebPagePreview: Boolean = false,
    disableNotification: Boolean = false
) : TelegramRequest(chatId, replyKeyboard, disableNotification)
