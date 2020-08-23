package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class InputLocationMessageContent(
    @JsonProperty("latitude")
    val latitude: Float,
    @JsonProperty("longitude")
    val longitude: Float,
    @JsonProperty("live_period")
    val livePeriod: Int? = null
) : InputMessageContent
