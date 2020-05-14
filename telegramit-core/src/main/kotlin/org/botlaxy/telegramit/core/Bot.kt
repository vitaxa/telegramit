package org.botlaxy.telegramit.core

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.Url
import mu.KotlinLogging
import okhttp3.*
import org.botlaxy.telegramit.core.client.*
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.conversation.persistence.ConversationPersistence
import org.botlaxy.telegramit.core.handler.dsl.Handler
import org.botlaxy.telegramit.core.handler.filter.CancelUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.HandlerUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.UnknownUpdateFilter
import org.botlaxy.telegramit.core.handler.loader.HandlerScriptManager
import org.botlaxy.telegramit.core.handler.loader.compile.KotlinScriptCompiler
import org.botlaxy.telegramit.core.listener.FilterUpdateListener
import java.net.PasswordAuthentication
import java.net.Proxy
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@DslMarker
annotation class BotDsl

fun bot(body: Bot.BotBuilder.() -> Unit) = Bot.BotBuilder().build(body)

class Bot private constructor(
    val name: String,
    val token: String,
    val telegramClientConfig: TelegramClientConfig,
    val proxyConfig: ProxyConfig?,
    val updateListener: UpdateListener?,
    val updateFilters: List<TelegramUpdateFilter>?,
    val conversationPersistenceConfig: ConversationPersistenceConfig?,
    val handlerScriptConfig: HandlerScriptConfig?
) {

    private var telegramClient: TelegramClient? = null

    private var telegramHttpClient: HttpClient? = null;

    private var conversationManager: ConversationManager? = null

    private var handlerScriptManager: HandlerScriptManager? = null

    fun start() {
        telegramHttpClient = buildHttpClient(proxyConfig)
        val telegramApi = TelegramApi(telegramHttpClient!!, token)

        val handlerHotReload = handlerScriptConfig?.handlerHotReload ?: false
        val handlerScriptPath = handlerScriptConfig?.handlerScriptPath
        handlerScriptManager =
            HandlerScriptManager(
                KotlinScriptCompiler(),
                handlerScriptPath,
                handlerHotReload
            ) { oldHandler, newHandler ->
                logger.debug { "Handler was changed. From $oldHandler to $newHandler" }
                val clearConversation: Boolean = conversationPersistenceConfig?.clearOnHandlerChange ?: true
                if (clearConversation) {
                    conversationManager?.clearAllConversation()
                }
                if (oldHandler != null) {
                    conversationManager?.removeHandler(oldHandler)
                }
                conversationManager?.addHandler(newHandler)
            }
        val handlers: List<Handler> = handlerScriptManager!!.compileHandlerFiles()
        conversationManager = ConversationManager(
            telegramApi,
            handlers,
            conversationPersistenceConfig?.conversationPersistence
        )
        val customUpdateFilters = updateFilters ?: emptyList()
        val filters = arrayOf(
            *customUpdateFilters.toTypedArray(),
            CancelUpdateFilter(conversationManager!!),
            HandlerUpdateFilter(conversationManager!!),
            UnknownUpdateFilter(telegramApi, conversationManager!!)
        )
        val updListener = updateListener ?: FilterUpdateListener(filters)

        telegramClient = resolveTelegramClient(telegramApi, updListener, telegramClientConfig)
        telegramClient?.start()
        logger.info { "Bot '$name' successfully started" }
    }

    fun stop() {
        telegramClient?.close()
        telegramHttpClient?.close()
        handlerScriptManager?.closeWatchHandler()
        logger.info { "Bot '$name' successfully stopped" }
    }

    private fun resolveTelegramClient(
        telegramApi: TelegramApi,
        updateListener: UpdateListener,
        telegramClientConfig: TelegramClientConfig
    ): TelegramClient {
        return when (telegramClientConfig.telegramClientType) {
            TelegramClientType.POLLING -> {
                TelegramPollingClient(
                    telegramApi,
                    updateListener,
                    telegramClientConfig as TelegramPoolingClientConfig
                )
            }
            TelegramClientType.WEBHOOK -> {
                TelegramWebhookClient(
                    telegramApi,
                    updateListener,
                    token,
                    telegramClientConfig as TelegramWebhookClientConfig
                )
            }
        }
    }

    private fun buildHttpClient(proxyConfig: ProxyConfig?): HttpClient {
        return HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = JacksonSerializer() {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                }
            }
            if (logger.isDebugEnabled) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.HEADERS
                }
            }
            engine {
                config {
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                }
            }
            proxyConfig?.let {
                if (proxyConfig.host != null && proxyConfig.port != null) {
                    engine {
                        if (proxyConfig.type == Proxy.Type.HTTP) {
                            proxy = ProxyBuilder.http(Url("http://${proxyConfig.host}:${proxyConfig.port}"))
                        } else if (proxyConfig.type == Proxy.Type.SOCKS) {
                            proxy = ProxyBuilder.socks(host = proxyConfig.host, port = proxyConfig.port)
                        }
                        // Proxy auth
                        if (proxyConfig.login != null && proxyConfig.password != null) {
                            java.net.Authenticator.setDefault(object : java.net.Authenticator() {
                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                    return PasswordAuthentication(proxyConfig.login, proxyConfig.password.toCharArray())
                                }
                            })
                            if (proxyConfig.type == Proxy.Type.HTTP) {
                                config {
                                    val proxyAuthenticator: Authenticator = object : Authenticator {
                                        override fun authenticate(route: Route?, response: Response): Request? {
                                            val credential: String =
                                                Credentials.basic(proxyConfig.login, proxyConfig.password)
                                            return response.request().newBuilder()
                                                .header("Proxy-Authorization", credential)
                                                .build()
                                        }
                                    }
                                    proxyAuthenticator(proxyAuthenticator)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @BotDsl
    class BotBuilder internal constructor() {

        lateinit var name: String

        lateinit var token: String

        var telegramClientConfig: TelegramClientConfig = TelegramPoolingClientConfig(TelegramClientType.POLLING)

        var proxyConfig: ProxyConfig? = null

        var updatesListener: UpdateListener? = null

        var updateFilters: List<TelegramUpdateFilter>? = null

        var persistenceConfig: ConversationPersistenceConfig? = null

        var handlerScriptConfig: HandlerScriptConfig? = null

        fun name(block: BotBuilder.() -> String) = apply { name = block() }

        fun token(block: BotBuilder.() -> String) = apply { token = block() }

        fun client(block: TelegramClientBuilder.() -> Unit) = apply {
            telegramClientConfig = TelegramClientBuilder().apply(block).build()
        }

        fun proxy(block: ProxyConfigBuilder.() -> Unit) = apply {
            proxyConfig = ProxyConfigBuilder().apply(block).build()
        }

        fun updatesListener(block: BotBuilder.() -> UpdateListener) = apply {
            updatesListener = block()
        }

        fun updateFilters(block: UpdateFilterBuilder.() -> Unit) = apply {
            updateFilters = UpdateFilterBuilder().build(block)
        }

        fun persistenceConfig(block: ConversationPersistenceConfigBuilder.() -> Unit) = apply {
            persistenceConfig = ConversationPersistenceConfigBuilder().apply(block).build()
        }

        fun handlerScriptConfig(block: HandlerScriptConfigBuilder.() -> Unit) = apply {
            handlerScriptConfig = HandlerScriptConfigBuilder().apply(block).build()
        }

        fun build(body: BotBuilder.() -> Unit): Bot {
            body()
            return Bot(
                name,
                token,
                telegramClientConfig,
                proxyConfig,
                updatesListener,
                updateFilters,
                persistenceConfig,
                handlerScriptConfig
            )
        }
    }

    @BotDsl
    class UpdateFilterBuilder {
        private val filters = arrayListOf<TelegramUpdateFilter>()

        fun filter(filter: TelegramUpdateFilter) {
            filters.add(filter)
        }

        fun build(body: UpdateFilterBuilder.() -> Unit): ArrayList<TelegramUpdateFilter> {
            body()
            return filters
        }
    }

    data class ConversationPersistenceConfig(
        val conversationPersistence: ConversationPersistence?,
        val clearOnHandlerChange: Boolean = true
    )

    @BotDsl
    class ConversationPersistenceConfigBuilder {
        var conversationPersistence: ConversationPersistence? = null
        var clearOnHandlerChange: Boolean = true

        fun build(): ConversationPersistenceConfig {
            return ConversationPersistenceConfig(conversationPersistence, clearOnHandlerChange)
        }

    }

    data class HandlerScriptConfig(
        val handlerScriptPath: String?,
        val handlerHotReload: Boolean
    )

    @BotDsl
    class HandlerScriptConfigBuilder {
        var handlerScriptPath: String? = null
        var handlerHotReload: Boolean = false

        fun build(): HandlerScriptConfig {
            return HandlerScriptConfig(handlerScriptPath, handlerHotReload)
        }
    }

    data class ProxyConfig(
        val host: String?,
        val port: Int?,
        val type: Proxy.Type,
        val login: String?,
        val password: String?
    )

    @BotDsl
    class ProxyConfigBuilder {
        var host: String? = null
        var port: Int? = null
        var type: Proxy.Type = Proxy.Type.DIRECT
        var login: String? = null
        var password: String? = null

        fun build(): ProxyConfig {
            return ProxyConfig(host, port, type, login, password)
        }
    }

    @BotDsl
    class TelegramPoolingClientBuilder {
        var timeout: Int? = null
        var limit: Int? = null

        fun build(): TelegramPoolingClientConfig {
            return TelegramPoolingClientConfig(TelegramClientType.POLLING, timeout, limit)
        }
    }

    @BotDsl
    class TelegramClientBuilder {
        lateinit var type: TelegramClientType
        var host: String? = null
        var port: Int? = null
        var timeout: Int? = null
        var limit: Int? = null

        fun build(): TelegramClientConfig {
            return when (type) {
                TelegramClientType.POLLING -> {
                    TelegramPoolingClientConfig(type, timeout, limit)
                }
                TelegramClientType.WEBHOOK -> {
                    TelegramWebhookClientConfig(type, host, port)
                }
            }
        }
    }

    class TelegramPoolingClientConfig(
        telegramClientType: TelegramClientType,
        val timeoutInSec: Int? = null,
        val limit: Int? = null
    ) : TelegramClientConfig(telegramClientType)

    class TelegramWebhookClientConfig(
        telegramClientType: TelegramClientType,
        val host: String?,
        val port: Int?
    ) : TelegramClientConfig(telegramClientType)

    abstract class TelegramClientConfig(val telegramClientType: TelegramClientType) {
    }

}
