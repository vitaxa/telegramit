package org.botlaxy.telegramit.core.handler.filter

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.botlaxy.telegramit.core.client.model.TelegramParseMode
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.extension.getChatId

private val logger = KotlinLogging.logger {}

class UnknownUpdateFilter(
    private val telegramApi: TelegramApi
) : TelegramUpdateFilter {

    override fun handleUpdate(
        update: TelegramUpdate,
        filterChain: TelegramUpdateFilterChain
    ) {
        val chatId = update.getChatId()
        logger.trace { "Execute 'UnknownFilter' '${chatId}'" }
        if (update.message?.chat?.type.equals("private", ignoreCase = true)) {
            val telegramChatRequest =
                TelegramChatRequest(chatId, "Unknown message", TelegramParseMode.MARKDOWN)
            telegramApi.sendMessage(telegramChatRequest)
        }
    }

}
