package org.botlaxy.telegramit.core.conversation

internal interface ConversationStatePublisher {

    fun register(conversationStateSubscriber: ConversationStateSubscriber)

    fun remove(conversationStateSubscriber: ConversationStateSubscriber)

    fun onUpdate(conversationState: ConversationState?)
}
