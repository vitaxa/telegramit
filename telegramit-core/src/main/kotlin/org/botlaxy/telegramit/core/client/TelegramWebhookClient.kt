package org.botlaxy.telegramit.core.client

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import org.botlaxy.telegramit.core.Bot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import java.net.URLEncoder

private val logger = KotlinLogging.logger {}

class TelegramWebhookClient(
    private val telegramApi: TelegramApi,
    private val updateListener: UpdateListener,
    botToken: String,
    clientConfig: Bot.TelegramWebhookClientConfig
) : TelegramClient {

    private val path: String = "/hooker/${URLEncoder.encode(botToken, java.nio.charset.StandardCharsets.UTF_8)}"

    private val host: String = clientConfig.host ?: DEFAULT_HOST

    private val port: Int = clientConfig.port ?: DEFAULT_PORT

    private val server = embeddedServer(Netty, port, host) {
        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            }
        }
        routing {
            post(path) {
                val update = call.receive<TelegramUpdate>()
                onUpdate(update)
                call.respondText { "True" }
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
        server.stop(GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
    }

    companion object {
        private val DEFAULT_HOST: String = "localhost"
        private val DEFAULT_PORT: Int = 8080
        private val GRACE_PERIOD_MILLIS: Long = 10000
        private val TIMEOUT_MILLIS: Long = 30000
    }

}
