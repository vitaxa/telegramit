package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.client.model.TelegramUser

data class CallbackQuery(
    val id: String,
    val from: TelegramUser,
    val message: TelegramMessage?,
    @JsonProperty("inline_message_id")
    val inlineMessageId: String?,
    @JsonProperty("chat_instance")
    val chatInstance: String?,
    val data: String?,
    @JsonProperty("game_short_name")
    val gameShortName: String?
)
