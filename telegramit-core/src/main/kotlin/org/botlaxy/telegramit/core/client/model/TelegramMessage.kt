package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramMessage(
    @JsonProperty("message_id")
    val id: Long,

    @JsonProperty("from")
    val user: TelegramUser?,

    @JsonProperty("date")
    val date: Long,

    @JsonProperty("chat")
    val chat: TelegramChat,

    @JsonProperty("text")
    val text: String?,

    @JsonProperty("forward_from")
    val forwardFrom: TelegramUser?,

    @JsonProperty("forward_from_chat")
    val forwardFromChat: TelegramChat?,

    @JsonProperty("audio")
    val audio: TelegramAudio?,

    @JsonProperty("document")
    val document: TelegramDocument?,

    @JsonProperty("photo")
    val photo: List<TelegramPhotoSize>?,

    @JsonProperty("contact")
    val contact: TelegramContact?
)
