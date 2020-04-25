package org.botlaxy.telegramit.core.conversation.persistence

interface ConversationPersistence {

    fun saveConversation(id: String, conversationData: ConversationData)

    fun getConversation(id: String): ConversationData?

    fun deleteConversation(id: String)

    fun clearConversations()

}
