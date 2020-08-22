package org.botlaxy.telegramit.core.handler.filter

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.extension.getChatId
import org.botlaxy.telegramit.core.extension.getMessage
import org.botlaxy.telegramit.core.handler.HandlerNotFound

private val logger = KotlinLogging.logger {}

class HandlerUpdateFilter(private val conversationManager: ConversationManager) : TelegramUpdateFilter {

    override fun handleUpdate(
        update: TelegramUpdate,
        filterChain: TelegramUpdateFilterChain
    ) {
        val chatId = update.getChatId()
        logger.trace { "Execute 'HandlerUpdateFilter' '${chatId}'" }
        if (update.getMessage() == null) {
            filterChain.doFilter(update)
        }
        try {
            val msg = update.getMessage()!!
            val conversationSession = conversationManager.getConversation(chatId)
            conversationSession.processMessage(msg)
        } catch (e: HandlerNotFound) {
            logger.warn(e.message)
            filterChain.doFilter(update)
        } catch (e: Exception) {
            logger.error(e) { "Unexpected exception during conversation process" }
        }
    }

}
