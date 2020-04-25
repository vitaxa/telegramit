package org.botlaxy.telegramit.core.conversation

class ConversationSessionException(val chatId: Long, message: String?) : RuntimeException(message)
