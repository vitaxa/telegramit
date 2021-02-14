package org.botlaxy.telegramit.spring.handler.dsl

import org.botlaxy.telegramit.core.handler.dsl.*
import org.springframework.context.support.GenericApplicationContext

class SpringInlineTelegramHandler(
    val answers: List<QueryAnswer>,
    val option: OptionConfig?,
    val processChosenResult: InlineChosenResultBlock?,
    val context: GenericApplicationContext,
) : TelegramHandler {

    inline fun <reified T> getBean(): T {
        return context.getBean(T::class.java)
    }

    override fun type(): TelegramHandlerType {
        return TelegramHandlerType.INLINE
    }

}
