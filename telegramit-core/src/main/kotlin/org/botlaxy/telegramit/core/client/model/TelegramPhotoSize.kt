package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramPhotoSize(

    @JsonProperty("file_id")
    val fileId: String,

    @JsonProperty("file_unique_id")
    val fileUniqueId: String,

    @JsonProperty("width")
    val width: String,

    @JsonProperty("file_size")
    val fileSize: String

)
