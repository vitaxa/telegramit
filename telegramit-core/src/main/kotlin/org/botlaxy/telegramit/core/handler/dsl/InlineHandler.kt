package org.botlaxy.telegramit.core.handler.dsl

class InlineHandler(
    val answers: List<QueryAnswer>,
    val option: OptionConfig?,
    val processChosenResult: InlineChosenResultBlock?
) : Handler {

    override fun type(): HandlerType {
        return HandlerType.INLINE
    }

}
