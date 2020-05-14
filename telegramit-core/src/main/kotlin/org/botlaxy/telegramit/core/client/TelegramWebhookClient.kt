package org.botlaxy.telegramit.core.client

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respondText
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
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
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
        val engineConnectorConfig = server.environment.connectors.first()
        val scheme = engineConnectorConfig.type.name.toLowerCase()
        val url = "$scheme://${engineConnectorConfig.host}:${engineConnectorConfig.port}$path"
        logger.debug { "Start Telegram webhook client. Url: $url" }
        server.start()
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
        const val DEFAULT_HOST: String = "localhost"
        const val DEFAULT_PORT: Int = 8080
        const val GRACE_PERIOD_MILLIS: Long = 10000
        const val TIMEOUT_MILLIS: Long = 30000
    }

}
