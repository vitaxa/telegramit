package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChatMember(
    @get:JsonProperty("user")
    val user: TelegramUser,
    @get:JsonProperty("status")
    val status: String,
    @get:JsonProperty("custom_title")
    val customTitle: String?,
    @get:JsonProperty("until_date")
    val untilDate: Int?,
    @get:JsonProperty("can_be_edited")
    val canBeEdited: Boolean?,
    @get:JsonProperty("can_post_messages")
    val canPostMessages: Boolean?,
    @get:JsonProperty("can_edit_messages")
    val canEditMessages: Boolean?,
    @get:JsonProperty("can_delete_messages")
    val canDeleteMessages: Boolean?,
    @get:JsonProperty("can_restrict_members")
    val canRestrictMembers: Boolean?,
    @get:JsonProperty("can_promote_members")
    val canPromoteMembers: Boolean?,
    @get:JsonProperty("can_change_info")
    val canChangeInfo: Boolean?,
    @get:JsonProperty("can_invite_users")
    val canInviteUsers: Boolean?,
    @get:JsonProperty("can_pin_messages")
    val canPinMessages: Boolean?,
    @get:JsonProperty("is_member")
    val isMember: Boolean?,
    @get:JsonProperty("can_send_messages")
    val canSendMessage: Boolean?,
    @get:JsonProperty("can_send_media_messages")
    val canSendMediaMessages: Boolean?,
    @get:JsonProperty("can_send_polls")
    val canSendPolls: Boolean?,
    @get:JsonProperty("can_send_other_messages")
    val canSendOtherMessages: Boolean?,
    @get:JsonProperty("can_add_web_page_previews")
    val canAddWebPagePreviews: Boolean?
)
