package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramPromoteChatMemberRequest(
    @get:JsonProperty("chat_id")
    val chatId: Long,
    @get:JsonProperty("user_id")
    val userId: Long,
    @get:JsonProperty("can_change_info")
    var canChangeInfo: Boolean,
    @get:JsonProperty("can_post_messages")
    var canPostMessages: Boolean,
    @get:JsonProperty("can_edit_messages")
    var canEditMessages: Boolean,
    @get:JsonProperty("can_delete_messages")
    var canDeleteMessages: Boolean,
    @get:JsonProperty("can_invite_users")
    var canInviteUsers: Boolean,
    @get:JsonProperty("can_restrict_members")
    var canRestrictMembers: Boolean,
    @get:JsonProperty("can_pin_messages")
    var canPinMessages: Boolean,
    @get:JsonProperty("can_promote_members")
    var canPromoteMembers: Boolean
)
