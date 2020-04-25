package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramAudio(

    @JsonProperty("file_id")
    val fileId: String,

    @JsonProperty("file_unique_id")
    val fileUniqueId: String,

    @JsonProperty("duration")
    val duration: Int,

    @JsonProperty("performer")
    val performer: String,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("mime_type")
    val mimeType: String,

    @JsonProperty("file_size")
    val fileSize: Int

)
