package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramChatPermissions(
    @get:JsonProperty("can_send_messages")
    var canSendMesssage: Boolean,
    @get:JsonProperty("can_send_media_messages")
    var canSendMediaMessages: Boolean,
    @get:JsonProperty("can_send_polls")
    var canSendPolls: Boolean,
    @get:JsonProperty("can_send_other_messages")
    var canSendOtherMessages: Boolean,
    @get:JsonProperty("can_add_web_page_previews")
    var canAddWebPagePreviews: Boolean,
    @get:JsonProperty("can_change_info")
    var canChangeInfo: Boolean,
    @get:JsonProperty("can_invite_users")
    var canInviteUsers: Boolean,
    @get:JsonProperty("can_pin_messages")
    var canPinMessages: Boolean
)
