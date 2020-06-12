package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

class TelegramVenueRequest(
    chatId: Long,
    @get:JsonProperty("latitude")
    val latitude: Float,
    @get:JsonProperty("longitude")
    val longitude: Float,
    @get:JsonProperty("title")
    val title: String,
    @get:JsonProperty("address")
    val address: String,
    @get:JsonProperty("foursquare_id")
    var foursquareId: String,
    @get:JsonProperty("foursquare_type")
    var foursquareType: String,
    replyKeyboard: TelegramReplyKeyboard? = null,
    disableNotification: Boolean = false
) : TelegramRequest(chatId, replyKeyboard, disableNotification)
