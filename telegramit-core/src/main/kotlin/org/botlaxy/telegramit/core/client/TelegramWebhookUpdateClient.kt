package org.botlaxy.telegramit.core.client

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging
import org.botlaxy.telegramit.core.TelegramBot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramUpdate

private val logger = KotlinLogging.logger {}

class TelegramWebhookUpdateClient(
    private val telegramApi: TelegramApi,
    private val updateListener: UpdateListener,
    botToken: String,
    private val clientConfig: TelegramBot.TelegramWebhookClientConfig
) : TelegramUpdateClient {

    private val path: String = "/hooker/${botToken.split(":")[1].encodeURLPath()}"

    private val env = applicationEngineEnvironment {
        sslConnector(
            keyStore = clientConfig.keyStore,
            keyAlias = clientConfig.keyAlias,
            keyStorePassword = { clientConfig.keyStorePassword.toCharArray() },
            privateKeyPassword = { clientConfig.privateKeyPassword.toCharArray() }) {
            host = clientConfig.host ?: DEFAULT_HOST
            port = clientConfig.port ?: DEFAULT_PORT
            keyStorePath = clientConfig.keyStoreFile.absoluteFile
        }
        module {
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
    }

    private val server = embeddedServer(Netty, env)

    override fun start() {
        val engineConnectorConfig = server.environment.connectors.first()
        val scheme = engineConnectorConfig.type.name.toLowerCase()
        val url = "${scheme}://${engineConnectorConfig.host}:${engineConnectorConfig.port}$path"
        logger.debug { "Start Telegram webhook client. Url: $url" }
        server.start()
        telegramApi.setWebhook(url, clientConfig.publicKeyFile)
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
        const val DEFAULT_HOST: String = "0.0.0.0"
        const val DEFAULT_PORT: Int = 80
        const val GRACE_PERIOD_MILLIS: Long = 10000
        const val TIMEOUT_MILLIS: Long = 30000
    }

}
