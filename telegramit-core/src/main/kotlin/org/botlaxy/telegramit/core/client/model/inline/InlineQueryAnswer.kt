package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonProperty

data class InlineQueryAnswer(
    @JsonProperty("inline_query_id")
    val inlineQueryId: String,
    @JsonProperty("results")
    val results: List<InlineQueryResult>,
    @JsonProperty("cache_time")
    val cacheTime: Int? = null,
    @JsonProperty("is_personal")
    val isPersonal: Boolean = false,
    @JsonProperty("next_offset")
    val nextOffset: String? = null,
    @JsonProperty("switch_pm_text")
    val switchPmText: String? = null,
    @JsonProperty("switch_pm_parameter")
    val switchPmParameter: String? = null
)
