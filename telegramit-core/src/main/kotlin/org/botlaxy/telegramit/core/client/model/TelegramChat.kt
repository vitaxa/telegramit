package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChat(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("type")
    val type: String,

    @JsonProperty("title")
    val title: String?,

    @JsonProperty("username")
    val username: String?,

    @JsonProperty("first_name")
    val firstName: String?,

    @JsonProperty("last_name")
    val lastName: String?,

    @JsonProperty("photo")
    val photo: TelegramChatPhoto?,

    @JsonProperty("description")
    val description: String?,

    @JsonProperty("invite_link")
    val inviteLink: String?,

    @JsonProperty("pinned_message")
    val pinnedMessage: TelegramMessage?,

    @JsonProperty("permissions")
    val permissions: TelegramChatPermissions?,

    @JsonProperty("slow_mode_delay")
    val slowModeDelay: Int?,

    @JsonProperty("sticker_set_name")
    val stickerSetName: String?,

    @JsonProperty("can_set_sticker_set")
    val canSetStickerSet: Boolean?

)
