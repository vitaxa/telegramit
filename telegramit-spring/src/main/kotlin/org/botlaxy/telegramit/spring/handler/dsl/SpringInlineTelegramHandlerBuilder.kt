package org.botlaxy.telegramit.spring.handler.dsl

import org.botlaxy.telegramit.core.handler.dsl.InlineChosenResultBlock
import org.botlaxy.telegramit.core.handler.dsl.InlineQueryResultBlock
import org.botlaxy.telegramit.core.handler.dsl.QueryAnswer
import org.springframework.context.support.GenericApplicationContext

@DslMarker
annotation class SpringInlineHandlerDsl

fun springInlineHandler(body: SpringInlineTelegramHandlerBuilder.() -> Unit): SpringInlineHandlerDslWrapper {
    return { context ->
        val handlerBuilder = SpringInlineTelegramHandlerBuilder(context)
        handlerBuilder.build(body)
    }
}

@SpringInlineHandlerDsl
class SpringInlineTelegramHandlerBuilder(
    private val context: GenericApplicationContext,
) {

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

    internal fun build(body: SpringInlineTelegramHandlerBuilder.() -> Unit): SpringInlineTelegramHandler {
        body()
        return SpringInlineTelegramHandler(
            answers,
            option,
            chosenResult,
            context
        )
    }

}

@SpringInlineHandlerDsl
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

@SpringInlineHandlerDsl
class OptionConfig() {

    var cacheTime: Int? = null
    var isPersonal: Boolean? = null
    var nextOffset: String? = null
    var switchPmText: String? = null
    var switchPmParameter: String? = null

}

typealias SpringInlineHandlerDslWrapper = (GenericApplicationContext) -> SpringInlineTelegramHandler
