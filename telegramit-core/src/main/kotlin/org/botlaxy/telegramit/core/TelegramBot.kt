package org.botlaxy.telegramit.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import mu.KotlinLogging
import okhttp3.*
import org.botlaxy.telegramit.core.client.*
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.conversation.ConversationManager
import org.botlaxy.telegramit.core.conversation.ConversationStateSubscriber
import org.botlaxy.telegramit.core.conversation.persistence.ConversationPersistence
import org.botlaxy.telegramit.core.handler.dsl.InlineTelegramHandler
import org.botlaxy.telegramit.core.handler.dsl.StepTelegramHandler
import org.botlaxy.telegramit.core.handler.dsl.TelegramHandler
import org.botlaxy.telegramit.core.handler.dsl.TelegramHandlerType
import org.botlaxy.telegramit.core.handler.filter.CancelUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.HandlerUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.InlineUpdateFilter
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilter
import org.botlaxy.telegramit.core.handler.loader.DefaultHandlerScriptManager
import org.botlaxy.telegramit.core.handler.loader.DynamicHandlerScriptManager
import org.botlaxy.telegramit.core.handler.loader.HandlerChangeListener
import org.botlaxy.telegramit.core.handler.loader.HandlerScriptManager
import org.botlaxy.telegramit.core.handler.loader.collect.ClassPathScriptCollector
import org.botlaxy.telegramit.core.handler.loader.compile.DefaultScriptCompiler
import org.botlaxy.telegramit.core.listener.FilterUpdateListener
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.File
import java.net.PasswordAuthentication
import java.net.Proxy
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private val logger = KotlinLogging.logger {}

@DslMarker
annotation class BotDsl

fun bot(body: TelegramBot.BotBuilder.() -> Unit) = TelegramBot.BotBuilder().build(body)

class TelegramBot private constructor(
    val name: String,
    val token: String,
    val telegramClientConfig: TelegramClientConfig,
    val proxyConfig: ProxyConfig?,
    val updateListener: UpdateListener?,
    val updateFilters: List<TelegramUpdateFilter>?,
    val conversationPersistenceConfig: ConversationPersistenceConfig?,
    val handlerScriptConfig: HandlerScriptConfig?,
    val conversationStateSubscribers: List<ConversationStateSubscriber>?
) {

    private val running = AtomicBoolean()

    private var telegramUpdateClient: TelegramUpdateClient? = null

    private var telegramHttpClient: HttpClient? = null

    var telegramApi: TelegramApi? = null
        private set

    fun start() {
        if (running.getAndSet(true)) {
            logger.warn { "Bot '$name' already started" }
            return
        }
        telegramHttpClient = buildHttpClient(proxyConfig)
        telegramApi = TelegramApi(telegramHttpClient!!, token)
        val handlerScriptManager = handlerScriptConfig?.handlerScriptManager ?: newHandlerScriptManager()

        // Init scripts (compile and group by type)
        val handlers: List<TelegramHandler> = handlerScriptManager.compileScripts()
        val conversationHandler = handlers
            .filter { handler -> handler.type() == TelegramHandlerType.STEP_BY_STEP }
            .map { handler -> handler as StepTelegramHandler }
        val inlineHandler = handlers
            .find { handler -> handler.type() == TelegramHandlerType.INLINE } as? InlineTelegramHandler

        // Init conversation manager
        val conversationManager = ConversationManager(
            telegramApi!!,
            conversationHandler,
            conversationPersistenceConfig?.conversationPersistence,
            conversationStateSubscribers
        )
        if (handlerScriptManager is DynamicHandlerScriptManager) {
            // Following the change of handlers for the conversation status change
            handlerScriptManager.addHandlerChangeListener(newHandlerChangeListener(conversationManager))
        }

        // Init handlers
        val customUpdateFilters = updateFilters ?: emptyList()
        val filters = mutableListOf<TelegramUpdateFilter>(*customUpdateFilters.toTypedArray())
        inlineHandler?.let { filters.add(InlineUpdateFilter(it, telegramApi!!)) }
        filters.apply {
            add(CancelUpdateFilter())
            add(HandlerUpdateFilter())
        }
        val updListener = updateListener ?: FilterUpdateListener(filters, conversationManager)

        telegramUpdateClient = resolveTelegramClient(telegramApi!!, updListener, telegramClientConfig)
        telegramUpdateClient?.start()
        logger.info { "Bot '$name' successfully started" }
    }

    fun stop() {
        if (!running.getAndSet(false)) {
            logger.warn { "Bot '$name' has not been started" }
            return
        }
        telegramUpdateClient?.close()
        telegramHttpClient?.close()
        logger.info { "Bot '$name' successfully stopped" }
    }

    private fun newHandlerScriptManager(): HandlerScriptManager {
        return DefaultHandlerScriptManager(
            DefaultScriptCompiler(KotlinJsr223JvmLocalScriptEngineFactory()),
            ClassPathScriptCollector()
        )
    }

    private fun newHandlerChangeListener(conversationManager: ConversationManager): HandlerChangeListener {
        return { oldHandler, newHandler ->
            logger.debug { "Handler was changed. From $oldHandler to $newHandler" }
            if (newHandler.type() == TelegramHandlerType.STEP_BY_STEP) {
                val clearConversation: Boolean = conversationPersistenceConfig?.clearOnHandlerChange ?: true
                if (clearConversation) {
                    conversationManager.clearAllConversation()
                }
                if (oldHandler != null) {
                    conversationManager.removeHandler(oldHandler as StepTelegramHandler)
                }
                conversationManager.addHandler(newHandler as StepTelegramHandler)
            }
        }
    }

    private fun resolveTelegramClient(
        telegramApi: TelegramApi,
        updateListener: UpdateListener,
        telegramClientConfig: TelegramClientConfig
    ): TelegramUpdateClient {
        return when (telegramClientConfig.telegramClientType) {
            TelegramClientType.POLLING -> {
                TelegramPollingUpdateClient(
                    telegramApi,
                    updateListener,
                    telegramClientConfig as TelegramPoolingClientConfig
                )
            }
            TelegramClientType.WEBHOOK -> {
                TelegramWebhookUpdateClient(
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
                    setSerializationInclusion(JsonInclude.Include.NON_NULL)
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
                                            return response.request.newBuilder()
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

        var conversationStateListeners: MutableList<ConversationStateSubscriber>? = null

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

        fun addConversationStateListener(block: BotBuilder.() -> ConversationStateSubscriber) = apply {
            if (conversationStateListeners == null) {
                conversationStateListeners = mutableListOf()
            }
            conversationStateListeners!!.add(block())
        }

        fun build(body: BotBuilder.() -> Unit): TelegramBot {
            body()
            return TelegramBot(
                name,
                token,
                telegramClientConfig,
                proxyConfig,
                updatesListener,
                updateFilters,
                persistenceConfig,
                handlerScriptConfig,
                conversationStateListeners
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
        val handlerScriptManager: HandlerScriptManager?,
    )

    @BotDsl
    class HandlerScriptConfigBuilder {
        var handlerScriptManager: HandlerScriptManager? = null

        fun build(): HandlerScriptConfig {
            if (handlerScriptManager == null) {
                handlerScriptManager =
                    DefaultHandlerScriptManager(
                        DefaultScriptCompiler(KotlinJsr223JvmLocalScriptEngineFactory()),
                        ClassPathScriptCollector()
                    )
            }
            return HandlerScriptConfig(handlerScriptManager)
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
        var sslConfig: SslConfig? = null

        fun build(): TelegramClientConfig {
            return when (type) {
                TelegramClientType.POLLING -> {
                    TelegramPoolingClientConfig(type, timeout, limit)
                }
                TelegramClientType.WEBHOOK -> {
                    TelegramWebhookClientConfig(
                        type,
                        host,
                        port,
                        sslConfig
                    )
                }
            }
        }

        fun sslConfig(block: SslConfigBuilder.() -> Unit) {
            sslConfig = SslConfigBuilder().apply(block).build()
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
        val port: Int?,
        val sslConfig: SslConfig?
    ) : TelegramClientConfig(telegramClientType)

    abstract class TelegramClientConfig(val telegramClientType: TelegramClientType) {
    }

    @BotDsl
    class SslConfigBuilder {
        var keyStore: KeyStore? = null
        var keyAlias: String? = null
        var keyStorePassword: String? = null
        var privateKeyPassword: String? = null
        var keyStoreFile: File? = null
        var publicKeyFile: File? = null

        fun build(): SslConfig {
            return SslConfig(
                keyStore ?: throw IllegalStateException("'keyStore' can't be null"),
                keyAlias ?: throw IllegalStateException("'keyAlias' can't be null"),
                keyStorePassword ?: throw IllegalStateException("'keyStorePassword' can't be null"),
                privateKeyPassword ?: throw IllegalStateException("'privateKeyPassword' can't be null"),
                keyStoreFile ?: throw IllegalStateException("'keyStoreFile' can't be null"),
                publicKeyFile ?: throw IllegalStateException("'publicKeyFile' can't be null")
            )
        }
    }

    data class SslConfig(
        val keyStore: KeyStore,
        val keyAlias: String,
        val keyStorePassword: String,
        val privateKeyPassword: String,
        val keyStoreFile: File,
        val publicKeyFile: File,
    )

}
