package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.botlaxy.telegramit.core.client.model.inline.CallbackQuery
import org.botlaxy.telegramit.core.client.model.inline.ChosenInlineQuery
import org.botlaxy.telegramit.core.client.model.inline.InlineQuery

data class TelegramUpdate(
    @JsonProperty("update_id")
    val id: Long,

    @JsonProperty("message")
    val message: TelegramMessage?,

    @JsonProperty("edited_message")
    val editedMessage: TelegramMessage?,

    @JsonProperty("channel_post")
    val channelPost: TelegramMessage?,

    @JsonProperty("edited_channel_post")
    val editedChannelPost: TelegramMessage?,

    @JsonProperty("inline_query")
    val inlineQuery: InlineQuery?,

    @JsonProperty("chosen_inline_result")
    val chosenInlineResult: ChosenInlineQuery?,

    @JsonProperty("callback_query")
    val callbackQuery: CallbackQuery?,
)
