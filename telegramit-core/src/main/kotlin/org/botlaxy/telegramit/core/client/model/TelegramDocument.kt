package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramDocument(
    @JsonProperty("file_id")
    val fileId: String,

    @JsonProperty("file_unique_id")
    val fileUniqueId: String,

    @JsonProperty("file_name")
    val fileName: String,

    @JsonProperty("mime_type")
    val mimeType: String,

    @JsonProperty("file_size")
    val fileSize: String
)
