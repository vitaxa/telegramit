package org.botlaxy.telegramit.core.client

import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import org.botlaxy.telegramit.core.Bot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramUpdate

private val logger = KotlinLogging.logger {}

class TelegramWebhookClient(
    private val telegramApi: TelegramApi,
    private val updateListener: UpdateListener,
    botToken: String,
    clientConfig: Bot.TelegramWebhookClientConfig
) : TelegramClient {

    private val path: String = "/hooker/$botToken"

    private val host: String = clientConfig.host ?: DEFAULT_HOST

    private val port: Int = clientConfig.port ?: DEFAULT_PORT

    private val server = embeddedServer(Netty, port, host) {
        routing {
            get(path) {
                onUpdate(ctx.body<TelegramUpdate>())
                ctx.result("ok")
            }
        }
    }

    override fun start() {
        val url = "http://$host:$port$path"
        logger.debug { "Start Telegram webhook client. Webhook url: $url" }
        server.start(wait = true)
        telegramApi.setWebhook(url)
    }

    override fun onUpdate(update: TelegramUpdate) {
        logger.debug { "Got a new update event: $update" }
        updateListener.onUpdate(update)
    }

    override fun close() {
        telegramApi.setWebhook("") // remove webhook
        server.stop()
    }

    companion object {
        private val DEFAULT_HOST: String = "localhost"
        private val DEFAULT_PORT: Int = 8080
    }

}
