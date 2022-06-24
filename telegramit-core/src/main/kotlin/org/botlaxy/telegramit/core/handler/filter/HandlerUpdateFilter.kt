package org.botlaxy.telegramit.core.handler.filter

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.extension.getChatId
import org.botlaxy.telegramit.core.extension.getMessage
import org.botlaxy.telegramit.core.handler.HandlerNotFound

private val logger = KotlinLogging.logger {}

class HandlerUpdateFilter : TelegramUpdateFilter {

    override fun handleUpdate(
        update: TelegramUpdate,
        conversationManager: ConversationManager,
        filterChain: TelegramUpdateFilterChain
    ) {
        val chatId = update.getChatId()
        logger.trace { "Execute 'HandlerUpdateFilter' '${chatId}'" }
        if (update.getMessage() == null && update.callbackQuery == null) {
            filterChain.doFilter(update)
            return
        }
        try {
            val conversationSession = conversationManager.getConversation(chatId)
            if (update.getMessage() != null) {
                conversationSession.processMessage(update.getMessage()!!)
            } else if (update.callbackQuery != null) {
                if (conversationSession.conversationState == null) {
                    filterChain.doFilter(update) // Skip callback if conversation not started
                    return
                }
                conversationSession.processCallbackMessage(update.callbackQuery)
            }
        } catch (e: HandlerNotFound) {
            logger.warn(e.message)
            filterChain.doFilter(update)
        } catch (e: Exception) {
            logger.error(e) { "Unexpected exception during conversation process" }
        }
    }

}
