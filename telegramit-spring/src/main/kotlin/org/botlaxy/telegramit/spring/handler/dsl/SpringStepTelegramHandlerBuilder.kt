package org.botlaxy.telegramit.spring.handler.dsl

import org.botlaxy.telegramit.core.handler.CommandParser
import org.botlaxy.telegramit.core.handler.DefaultCommandParser
import org.botlaxy.telegramit.core.handler.HandlerException
import org.botlaxy.telegramit.core.handler.dsl.*
import org.springframework.context.support.GenericApplicationContext

@DslMarker
annotation class SpringStepHandlerDsl

fun springHandler(
    vararg commands: String,
    body: SpringStepTelegramHandlerBuilder.() -> Unit
): SpringHandlerDslWrapper {
    return { context ->
        val handlerBuilder = SpringStepTelegramHandlerBuilder(commands.asList(), context)
        handlerBuilder.build(body)
    }
}

@SpringStepHandlerDsl
class SpringStepTelegramHandlerBuilder(
    private val commands: List<String>,
    val context: GenericApplicationContext,
) {

    private val stepBuilders: MutableList<SpringStepBuilder<*>> = arrayListOf()
    private var process: ProcessBlock? = null
    private var commandParser: CommandParser = DefaultCommandParser()

    fun <T : Any> step(key: String, block: SpringStepBuilder<T>.() -> Unit): SpringStepBuilder<T> {
        val stepBuilder: SpringStepBuilder<T> = SpringStepBuilder<T>(key).apply(block)
        stepBuilders.add(stepBuilder)

        return stepBuilder
    }

    fun process(processor: ProcessBlock) {
        this.process = processor
    }

    internal fun build(body: SpringStepTelegramHandlerBuilder.() -> Unit): SpringStepTelegramHandler {
        body()
        val steps = arrayListOf<Step<*>>()
        for ((index, stepBuilder) in stepBuilders.withIndex()) {
            val step = stepBuilder.build {
                // Next step
                if (index + 1 < stepBuilders.size) {
                    stepBuilders[index + 1].key
                } else {
                    null
                }
            }
            steps.add(step)
        }
        val handlerCommands = commands.map { cmd -> commandParser.parse(cmd) }

        return SpringStepTelegramHandler(
            handlerCommands,
            steps.associateBy { it.key },
            process ?: throw HandlerException("Process block must not be null"),
            context
        )
    }

}

@SpringStepHandlerDsl
class SpringStepBuilder<T : Any>(val key: String) {

    private var entry: EntryBlock? = null
    private var validation: ValidationBlock? = null
    private var resolver: ResolverBlock<T>? = null
    private var next: NextStepBlock? = null

    fun entry(entry: EntryBlock) {
        this.entry = entry
    }

    fun validation(validation: ValidationBlock) {
        this.validation = validation
    }

    fun resolver(resolver: ResolverBlock<T>) {
        this.resolver = resolver
    }

    fun next(next: NextStepBlock) {
        this.next = next
    }

    internal fun build(defaultNext: NextStepBlock): Step<T> {
        return Step(
            key,
            entry ?: throw HandlerException("Step 'entry' block can't be null"),
            validation,
            resolver,
            next ?: defaultNext
        )
    }

}

typealias SpringHandlerDslWrapper = (GenericApplicationContext) -> SpringStepTelegramHandler
