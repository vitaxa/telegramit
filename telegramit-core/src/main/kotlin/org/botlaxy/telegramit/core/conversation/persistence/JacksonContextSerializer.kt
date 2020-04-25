package org.botlaxy.telegramit.core.conversation.persistence

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class JacksonContextSerializer : PersistenceContextSerializer {

    private val ptv: PolymorphicTypeValidator = BasicPolymorphicTypeValidator
        .builder()
        .allowIfBaseType(Any::class.java)
        .build()

    private val jsonMapper = jacksonObjectMapper()
        .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)

    override fun serialize(conversationContext: ConversationData): ByteArray {
        return jsonMapper.writeValueAsBytes(conversationContext)
    }

    override fun deserialize(byteArray: ByteArray): ConversationData {
        return jsonMapper.readValue<ConversationData>(byteArray)
    }

}
