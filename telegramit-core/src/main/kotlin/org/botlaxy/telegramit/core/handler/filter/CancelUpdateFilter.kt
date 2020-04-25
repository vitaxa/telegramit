package org.botlaxy.telegramit.core.handler.filter

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.extension.getChatId
import org.botlaxy.telegramit.core.extension.getMessage

private val logger = KotlinLogging.logger {}

class CancelUpdateFilter(private val conversationManager: ConversationManager) : TelegramUpdateFilter {

    override fun handleUpdate(
        update: TelegramUpdate,
        filterChain: TelegramUpdateFilterChain
    ) {
        val chatId = update.getChatId()
        logger.trace { "Execute 'CancelFilter' '${chatId}'" }
        val isCancel = update.getMessage()?.text.equals(DEFAULT_CANCEL_MSG, ignoreCase = true)
        if (isCancel) {
            conversationManager.closeConversation(chatId)
        } else {
            filterChain.doFilter(update)
        }
    }

    companion object {
        const val DEFAULT_CANCEL_MSG: String = "/cancel"
    }

}
