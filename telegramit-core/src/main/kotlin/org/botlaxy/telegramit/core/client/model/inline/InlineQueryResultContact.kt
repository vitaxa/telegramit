package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultContact(
    id: String,
    @JsonProperty("phone_number")
    val phoneNumber: String,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String?,
    @JsonProperty("vcard")
    val vcard: String?,
    @JsonProperty("reply_markup")
    val replyMarkup: TelegramInlineReplyKeyboardMarkup,
    @JsonProperty("input_message_content")
    val inputMessageContent: InputMessageContent?,
    @JsonProperty("thumb_url")
    val thumbUrl: String?,
    @JsonProperty("thumb_width")
    val thumbWidth: Int?,
    @JsonProperty("thumb_height")
    val thumbHeight: Int?
) : InlineQueryResult("contact", id)
