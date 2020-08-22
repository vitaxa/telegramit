package org.botlaxy.telegramit.core.client.model.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class InlineQueryResult(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("id")
    val id: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InlineQueryResult

        if (type != other.type) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}


