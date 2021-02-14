package org.botlaxy.telegramit.spring.handler.dsl

import org.botlaxy.telegramit.core.handler.HandlerCommand
import org.botlaxy.telegramit.core.handler.dsl.ProcessBlock
import org.botlaxy.telegramit.core.handler.dsl.Step
import org.botlaxy.telegramit.core.handler.dsl.StepTelegramHandler
import org.springframework.context.support.GenericApplicationContext

class SpringStepTelegramHandler(
    commands: List<HandlerCommand>,
    steps: Map<String, Step<*>>,
    process: ProcessBlock,
    val context: GenericApplicationContext,
) : StepTelegramHandler(commands, steps, process) {

    inline fun <reified T> getBean(): T {
        return context.getBean(T::class.java)
    }

}
