package org.botlaxy.telegramit.core.handler.dsl

import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.client.model.TelegramRequest
import org.botlaxy.telegramit.core.client.model.inline.*
import org.botlaxy.telegramit.core.conversation.ConversationContext

typealias EntryBlock = (ctx: ConversationContext, args: Map<String, String>) -> TelegramRequest
typealias ValidationBlock = (msg: TelegramMessage) -> TelegramRequest?
typealias CallbackValidationBlock = (callbackQuery: CallbackQuery) -> TelegramRequest?
typealias ResolverBlock<T> = (msg: TelegramMessage) -> T
typealias CallbackResolverBlock<T> = (callbackQuery: CallbackQuery) -> T
typealias NextStepBlock = (ctx: ConversationContext) -> String?

typealias ProcessBlock = (ctx: ConversationContext, args: Map<String, String>) -> TelegramRequest?

typealias InlineQueryResultBlock = (inlineQuery: InlineHandlerQuery) -> List<InlineQueryResult>
typealias InlineChosenResultBlock = (choseQuery: ChosenInlineQuery) -> Unit
