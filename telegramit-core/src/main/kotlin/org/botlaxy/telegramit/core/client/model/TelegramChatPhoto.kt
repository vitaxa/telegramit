package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChatPhoto(
    @get:JsonProperty("small_file_id")
    val smallFileId: String,
    @get:JsonProperty("small_file_unique_id")
    val smallFileUniqueId: String,
    @get:JsonProperty("big_file_id")
    val bigFileId: String,
    @get:JsonProperty("big_file_unique_id")
    val bigFileUniqueId: String
)
