package org.botlaxy.telegramit.core.handler.dsl

import java.lang.IllegalStateException

@DslMarker
annotation class InlineHandlerDsl

fun inlineHandler(body: InlineTelegramHandlerBuilder.() -> Unit): InlineTelegramHandler {
    return InlineTelegramHandlerBuilder().build(body)
}

@InlineHandlerDsl
class InlineTelegramHandlerBuilder() {

    private var option: OptionConfig? = null

    private var chosenResult: InlineChosenResultBlock? = null

    private val answers: MutableList<QueryAnswer> = arrayListOf()

    fun answer(block: AnswerBuilder.() -> Unit) {
        val answerBuilder = AnswerBuilder().apply(block)
        answers.add(answerBuilder.build())
    }

    fun option(block: OptionConfig.() -> Unit) {
        this.option = OptionConfig().apply(block)
    }

    fun chosen(block: InlineChosenResultBlock) {
        this.chosenResult = block
    }

    internal fun build(body: InlineTelegramHandlerBuilder.() -> Unit): InlineTelegramHandler {
        body()
        return InlineTelegramHandler(
            answers,
            option,
            chosenResult
        )
    }

}

@InlineHandlerDsl
class AnswerBuilder() {

    private var resultBlockList: MutableList<InlineQueryResultBlock> = arrayListOf()

    fun result(queryResultBlock: InlineQueryResultBlock) {
        resultBlockList.add(queryResultBlock)
    }

    internal fun build(): QueryAnswer {
        if (resultBlockList.isEmpty()) {
            throw IllegalStateException("You need to specify at least one 'result'")
        }
        return QueryAnswer(resultBlockList)
    }

}

@InlineHandlerDsl
class OptionConfig() {

    var cacheTime: Int? = null
    var isPersonal: Boolean? = null
    var nextOffset: String? = null
    var switchPmText: String? = null
    var switchPmParameter: String? = null

}
