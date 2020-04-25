package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginUrl(
    @JsonProperty("url")
    val url: String,
    @JsonProperty("forward_text")
    val forwardText: String? = null,
    @JsonProperty("bot_username")
    val botUsername: String? = null,
    @JsonProperty("request_write_access")
    val requestWriteAccess: Boolean = false
)
