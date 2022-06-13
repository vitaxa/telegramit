package org.botlaxy.telegramit.core.conversation

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.conversation.persistence.ConversationData
import org.botlaxy.telegramit.core.conversation.persistence.ConversationPersistence
import org.botlaxy.telegramit.core.handler.HandlerCommand
import org.botlaxy.telegramit.core.handler.dsl.StepTelegramHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

private val logger = KotlinLogging.logger {}

class ConversationManager(
    private val telegramApi: TelegramApi,
    private val handlers: List<StepTelegramHandler>,
    private val persistence: ConversationPersistence? = null,
    private val conversationStateSubscribers: List<ConversationStateSubscriber>? = null
) {

    private val conversationSessionMap: MutableMap<Long, ConversationSession> = ConcurrentHashMap()

    private val handlerMap: MutableMap<HandlerCommand, StepTelegramHandler> = ConcurrentHashMap()

    init {
        handlerMap.putAll(groupByCommand(handlers)) // Initial handlers
    }

    fun getConversation(chatId: Long): ConversationSession {
        logger.debug { "Get conversation by id: $chatId" }
        var conversationSession = conversationSessionMap[chatId]
        if (conversationSession == null) {
            // Try to load conversation from persistence storage
            val conversationState = loadConversationState(chatId, handlerMap)
            conversationSession = if (conversationState != null) {
                logger.debug { "Creating new conversation by state for : $chatId" }
                ConversationSession(chatId, telegramApi, handlerMap, conversationState) {
                    closeConversation(chatId)
                }
            } else {
                logger.debug { "Creating new conversation for: $chatId" }
                ConversationSession(chatId, telegramApi, handlerMap) { closeConversation(chatId) }
            }
            if (conversationStateSubscribers?.isNotEmpty() == true) {
                conversationStateSubscribers.forEach { conversationSession.register(it) }
            }
            persistence?.let {
                conversationSession.register(object : ConversationStateSubscriber {
                    override fun update(conversationState: ConversationState?, telegramApi: TelegramApi) {
                        if (conversationState != null) {
                            saveConversationState(chatId, conversationState)
                        }
                    }
                })
            }
        }
        conversationSessionMap[chatId] = conversationSession
        return conversationSession
    }

    fun closeConversation(chatId: Long) {
        logger.debug { "Remove conversation by id: $chatId" }
        conversationSessionMap.remove(chatId)
        persistence?.deleteConversation(chatId.toString())
    }

    fun clearAllConversation() {
        logger.trace { "Clear all conversation" }
        conversationSessionMap.clear()
        persistence?.clearConversations()
    }

    fun addHandler(handler: StepTelegramHandler) {
        val newHandlerMap = groupByCommand(listOf(handler))
        handlerMap.putAll(newHandlerMap)
    }

    fun removeHandler(handler: StepTelegramHandler) {
        val newHandlerMap = groupByCommand(listOf(handler))
        for (handlerCommand in newHandlerMap.keys) {
            handlerMap.remove(handlerCommand)
        }
    }

    private fun loadConversationState(
        chatId: Long,
        handlerMap: Map<HandlerCommand, StepTelegramHandler>
    ): ConversationState? {
        logger.debug { "Load '$chatId' conversation from persistence storage" }
        val conversationData = persistence?.getConversation(chatId.toString())
        if (conversationData != null) {
            var handlerCommand: HandlerCommand? = null
            var handler: StepTelegramHandler? = null
            for (entry in handlerMap) {
                if (entry.key.command.equals(conversationData.handlerCommand, ignoreCase = true)) {
                    handlerCommand = entry.key
                    handler = entry.value
                }
            }
            if (handler != null && handlerCommand != null) {
                val conversationContext =
                    ConversationContext(
                        telegramApi,
                        AtomicReference(conversationData.message),
                        AtomicReference(conversationData.callbackQuery),
                        conversationData.answer.toMutableMap(),
                    )
                val conversationState = ConversationState(
                    handlerCommand,
                    handler,
                    conversationContext,
                    ConversationStage.values().find { it.name.equals(conversationData.stage, true) }
                        ?: ConversationStage.UNKNOWN)
                conversationState.currentStep = handler.getStep(conversationData.stepKey)
                return conversationState
            }
        }
        logger.debug { "Conversation '$chatId' not found" }
        return null
    }

    private fun saveConversationState(chatId: Long, state: ConversationState) {
        logger.debug { "Save '$chatId' conversation to persistence storage" }
        val handlerCommand = state.handler.commands.first() // Save any handler command
        val conversationData = ConversationData(
            handlerCommand.command,
            state.currentStep!!.key,
            state.ctx.message,
            state.ctx.callback,
            state.ctx.answer,
            state.ctx.store,
            state.stage.name
        )
        persistence?.saveConversation(chatId.toString(), conversationData)
    }

    private fun groupByCommand(handlers: List<StepTelegramHandler>): HashMap<HandlerCommand, StepTelegramHandler> {
        val handlerMap = hashMapOf<HandlerCommand, StepTelegramHandler>()
        for (handler in handlers) {
            for (handlerCommand in handler.commands) {
                val prevValue = handlerMap.put(handlerCommand, handler)
                if (prevValue != null) {
                    throw IllegalArgumentException("${handlerCommand.command} is already in use")
                }
            }
        }
        return handlerMap
    }

}
