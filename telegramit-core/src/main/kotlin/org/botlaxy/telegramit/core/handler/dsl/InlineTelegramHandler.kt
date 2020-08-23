package org.botlaxy.telegramit.core.handler.dsl

class InlineTelegramHandler(
    val answers: List<QueryAnswer>,
    val option: OptionConfig?,
    val processChosenResult: InlineChosenResultBlock?
) : TelegramHandler {

    override fun type(): TelegramHandlerType {
        return TelegramHandlerType.INLINE
    }

}
