package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class InputContactMessageContent(
    @JsonProperty("phone_number")
    val phoneNumber: String,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String? = null,
    @JsonProperty("vcard")
    val vcard: String? = null
) : InputMessageContent
