package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramPollRequest(
    chatId: Long,
    @get:JsonProperty("question")
    val question: String,
    @get:JsonProperty("options")
    val options: Array<String>,
    @get:JsonProperty("is_anonymous")
    var isAnonymous: String,
    @get:JsonProperty("type")
    var type: String,
    @get:JsonProperty("allows_multiple_answers")
    var allowsMultipleAnswers: Boolean,
    @get:JsonProperty("correct_option_id")
    var correctOptionId: Int,
    @get:JsonProperty("explanation")
    var explanation: String,
    @get:JsonProperty("explanation_parse_mode")
    var explanationParseMode: String,
    @get:JsonProperty("open_period")
    var openPeriod: Int,
    @get:JsonProperty("close_date")
    var closeDate: Int,
    @get:JsonProperty("is_closed")
    var isClosed: Boolean,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false
) : TelegramRequest(chatId, replyKeyboard, disableNotification)
