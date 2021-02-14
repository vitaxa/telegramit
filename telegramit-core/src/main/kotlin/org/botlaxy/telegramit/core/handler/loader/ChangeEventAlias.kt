package org.botlaxy.telegramit.core.handler.loader

import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler

typealias HandlerChangeListener = (oldHandler: TelegramHandler?, newHandler: TelegramHandler) -> Unit
