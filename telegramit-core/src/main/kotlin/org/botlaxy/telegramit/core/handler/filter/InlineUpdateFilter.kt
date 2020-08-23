package org.botlaxy.telegramit.core.handler.filter

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import org.botlaxy.telegramit.core.client.model.inline.InlineQueryAnswer
import org.botlaxy.telegramit.core.client.model.inline.toHandlerQuery
import org.botlaxy.telegramit.core.extension.isInlineMessage
import org.botlaxy.telegramit.core.handler.dsl.InlineTelegramHandler

private val logger = KotlinLogging.logger {}

class InlineUpdateFilter(
    private val handler: InlineTelegramHandler,
    private val telegramApi: TelegramApi
) : TelegramUpdateFilter {

    override fun handleUpdate(update: TelegramUpdate, filterChain: TelegramUpdateFilterChain) {
        logger.trace { "Execute 'InlineUpdateFilter'" }
        if (!update.isInlineMessage()) {
            filterChain.doFilter(update)
        }
        if (update.inlineQuery != null) {
            val handlerQuery = update.inlineQuery.toHandlerQuery()
            val queryResultList = handler.answers
                .flatMap { it.result }
                .map { it(handlerQuery) }
                .flatten()
            val inlineQueryAnswer = InlineQueryAnswer(
                update.inlineQuery.id,
                queryResultList,
                handler.option?.cacheTime,
                handler.option?.isPersonal ?: false,
                handler.option?.nextOffset,
                handler.option?.switchPmText,
                handler.option?.switchPmParameter
            )
            val inlineSuccess = telegramApi.answerInlineQuery(inlineQueryAnswer)
            if (!inlineSuccess) {
                logger.warn { "Inline query answer was unsuccessful. QueryId='${inlineQueryAnswer.inlineQueryId}'" }
            }
        } else if (update.chosenInlineResult != null) {
            handler.processChosenResult?.let { block -> block(update.chosenInlineResult) }
        }
    }

}
