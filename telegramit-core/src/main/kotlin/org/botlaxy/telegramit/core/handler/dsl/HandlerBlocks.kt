package org.botlaxy.telegramit.core.handler.dsl

import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.client.model.TelegramRequest
import org.botlaxy.telegramit.core.client.model.inline.ChosenInlineQuery
import org.botlaxy.telegramit.core.client.model.inline.InlineHandlerQuery
import org.botlaxy.telegramit.core.client.model.inline.InlineQueryAnswer
import org.botlaxy.telegramit.core.client.model.inline.InlineQueryResult
import org.botlaxy.telegramit.core.conversation.ConversationContext

typealias EntryBlock = (ctx: ConversationContext, args: Map<String, String>) -> TelegramRequest
typealias ValidationBlock = (msg: TelegramMessage) -> TelegramRequest?
typealias ResolverBlock<T> = (msg: TelegramMessage) -> T
typealias NextStepBlock = (ctx: ConversationContext) -> String?

typealias ProcessBlock = (ctx: ConversationContext, args: Map<String, String>) -> TelegramRequest?

typealias InlineQueryResultBlock = (inlineQuery: InlineHandlerQuery) -> List<InlineQueryResult>
typealias InlineChosenResultBlock = (choseQuery: ChosenInlineQuery) -> Unit
