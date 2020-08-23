package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class InputVenueMessageContent(
    @JsonProperty("latitude")
    val latitude: Float,
    @JsonProperty("longitude")
    val longitude: Float,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("address")
    val address: String,
    @JsonProperty("foursquare_id")
    val foursquareId: String,
    @JsonProperty("foursquare_type")
    val foursquareType: String
) : InputMessageContent
