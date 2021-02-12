package org.botlaxy.telegramit.spring.client

import org.botlaxy.telegramit.core.TelegramBot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import javax.annotation.PreDestroy

class SpringTelegramBot(
    private val bot: TelegramBot
) : ApplicationListener<ApplicationReadyEvent> {

    val telegramApi: TelegramApi?
        get() = bot.telegramApi

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        start()
    }

    fun start() {
        bot.start()
    }

    @PreDestroy
    fun shutdown() {
        bot.stop()
    }

}
