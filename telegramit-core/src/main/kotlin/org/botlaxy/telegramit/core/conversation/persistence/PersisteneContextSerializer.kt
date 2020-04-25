package org.botlaxy.telegramit.core.conversation.persistence

interface PersistenceContextSerializer {

    fun serialize(conversationContext: ConversationData): ByteArray

    fun deserialize(byteArray: ByteArray): ConversationData

}
