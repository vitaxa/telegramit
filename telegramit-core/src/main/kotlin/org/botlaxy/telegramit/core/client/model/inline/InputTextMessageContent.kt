package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class InputTextMessageContent(
    @JsonProperty("message_text")
    val messageText: String,
    @JsonProperty("parse_mode")
    val parseMode: String? = null,
    @JsonProperty("disable_web_page_preview")
    val disableWebPagePreview: Boolean = false
) : InputMessageContent
