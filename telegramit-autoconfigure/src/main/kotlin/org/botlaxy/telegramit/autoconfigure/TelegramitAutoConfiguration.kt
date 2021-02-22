package org.botlaxy.telegramit.autoconfigure

import org.botlaxy.telegramit.autoconfigure.property.TelegramPropertiesValidator
import org.botlaxy.telegramit.autoconfigure.property.TelegramitProperties
import org.botlaxy.telegramit.core.bot
import org.botlaxy.telegramit.core.client.TelegramClientType
import org.botlaxy.telegramit.core.conversation.persistence.ConversationPersistence
import org.botlaxy.telegramit.core.handler.filter.TelegramUpdateFilter
import org.botlaxy.telegramit.core.handler.loader.DefaultHandlerScriptManager
import org.botlaxy.telegramit.core.handler.loader.HandlerScriptManager
import org.botlaxy.telegramit.core.handler.loader.collect.ClassPathScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.ScriptCollector
import org.botlaxy.telegramit.core.handler.loader.compile.HandlerScriptCompiler
import org.botlaxy.telegramit.spring.client.SpringTelegramBot
import org.botlaxy.telegramit.spring.handler.loader.compile.SpringScriptCompiler
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.validation.Validator

@Configuration
@ConditionalOnClass(SpringTelegramBot::class)
@EnableConfigurationProperties(TelegramitProperties::class)
class TelegramitAutoConfiguration(val telegramProperties: TelegramitProperties) {

    companion object {
        @Bean
        fun configurationPropertiesValidator(): Validator {
            return TelegramPropertiesValidator()
        }
    }

    @Bean
    @ConditionalOnMissingBean(name = ["telegramProperties"])
    fun telegramProperties(): TelegramitProperties = telegramProperties

    @Bean
    @ConditionalOnMissingBean(HandlerScriptCompiler::class)
    fun handlerScriptCompiler(context: GenericApplicationContext): HandlerScriptCompiler {
        return SpringScriptCompiler(context)
    }

    @Bean
    @ConditionalOnMissingBean(ScriptCollector::class)
    fun scriptCollector(): ScriptCollector {
        return ClassPathScriptCollector()
    }

    @Bean
    @ConditionalOnMissingBean(HandlerScriptManager::class)
    fun handlerScriptManager(
        handlerScriptCompiler: HandlerScriptCompiler,
        scriptCollector: ScriptCollector
    ): HandlerScriptManager {
        return DefaultHandlerScriptManager(handlerScriptCompiler, scriptCollector)
    }

    @Bean
    @ConditionalOnMissingBean(SpringTelegramBot::class)
    fun telegramBot(
        telegramProperties: TelegramitProperties,
        handlerScriptManager: HandlerScriptManager,
        persistence: ConversationPersistence?,
        filters: List<TelegramUpdateFilter>?
    ): SpringTelegramBot {
        val telegramBot = bot {
            name = telegramProperties.name
            token = telegramProperties.token
            updateFilters = filters
            if (telegramProperties.mode == TelegramClientType.WEBHOOK) {
                client {
                    type = telegramProperties.mode
                    host = telegramProperties.webhookHost
                    port = telegramProperties.webHookPort
                }
            }
            proxy {
                telegramProperties.proxyHost?.let { host = it }
                telegramProperties.proxyPort?.let { port = it }
                telegramProperties.proxyLogin?.let { login = it }
                telegramProperties.proxyPassword?.let { password = it }
                telegramProperties.proxyType?.let { type = it }
            }
            persistence?.let {
                persistenceConfig {
                    conversationPersistence = persistence
                }
            }
            handlerScriptConfig {
                this.handlerScriptManager = handlerScriptManager
            }
        }

        return SpringTelegramBot(telegramBot)
    }

}
