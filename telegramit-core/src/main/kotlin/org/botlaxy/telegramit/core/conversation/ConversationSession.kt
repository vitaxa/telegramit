package org.botlaxy.telegramit.core.conversation

import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.*
import org.botlaxy.telegramit.core.extension.emojize
import org.botlaxy.telegramit.core.handler.HandlerCommand
import org.botlaxy.telegramit.core.handler.HandlerNotFound
import org.botlaxy.telegramit.core.handler.dsl.Handler
import org.botlaxy.telegramit.core.request.TextMessage
import java.util.*
import java.util.concurrent.atomic.AtomicReference

private val logger = KotlinLogging.logger {}

class ConversationSession(
    val chatId: Long,
    private val telegramApi: TelegramApi,
    private val handlerMap: Map<HandlerCommand, Handler>,
    private var initialState: ConversationState? = null,
    private val finishCallback: (() -> Unit)? = null
) {

    var proccessUpdateFinishListener: ((ConversationState?) -> Unit)? = null

    private var conversationState: ConversationState? = initialState

    fun processMessage(message: TelegramMessage) {
        val currentState = conversationState

        // Determine handler
        val handlerHolder: HandlerHolder = getHandler(currentState, message.text)
        val (handlerCommand, handler) = handlerHolder

        // Process handler
        val conversationResponse: TelegramRequest? = try {
            if (currentState == null) {
                // Handle first entry step block
                conversationState = ConversationState(
                    handlerCommand,
                    handler,
                    ConversationContext(telegramApi, AtomicReference(message))
                )
                val command = message.text!!
                val commandParams = getCommandParams(command, handlerCommand.params)
                handleEntryStepBlock(conversationState!!, commandParams)
            } else {
                currentState.ctx.message = message
                val currentStep = currentState.currentStep!!

                // Validate message
                val validationMsg = currentStep.validation?.let { block ->
                    block(message)
                }
                if (validationMsg != null) {
                    return sendTelegramRequest(chatId, validationMsg)
                }

                // Resolve message
                val resolvedMsg = currentStep.resolver?.let { block ->
                    block(message)
                }
                if (resolvedMsg != null) {
                    currentState.ctx.answer[currentStep.key] = resolvedMsg
                } else {
                    currentState.ctx.answer[currentStep.key] = message
                }

                // Finalize or handle next entry step block
                val nextStepKey = currentStep.next(currentState.ctx)
                val nextStep = nextStepKey?.let { currentState.handler.getStep(nextStepKey) }
                currentState.currentStep = nextStep
                handleEntryStepBlock(currentState)
            }
        } catch (e: Exception) {
            logger.error(e) { "Unexpected exception during process update" }
            resetState()
            null
        }
        if (conversationResponse != null) {
            sendTelegramRequest(chatId, conversationResponse)
        }
        proccessUpdateFinishListener?.invoke(conversationState)
    }

    private fun handleEntryStepBlock(state: ConversationState, params: Map<String, String>? = null): TelegramRequest? {
        val step = state.currentStep
        return if (step != null) {
            step.entry(state.ctx, params ?: emptyMap())
        } else {
            try {
                state.handler.process(state.ctx, params ?: emptyMap())
            } finally {
                resetState()
            }
        }
    }

    private fun sendTelegramRequest(chatId: Long, request: TelegramRequest) {
        if (request.chatId <= 0) {
            request.chatId = chatId
        }
        when (request) {
            is TelegramChatRequest -> telegramApi.sendMessage(request)
            is TelegramAudioRequest -> telegramApi.sendAudio(request)
            is TelegramPhotoRequest -> telegramApi.sendPhoto(request)
            is TelegramVideoRequest -> telegramApi.sendVideo(request)
            is TelegramDocumentRequest -> telegramApi.sendDocument(request)
        }
    }

    private fun getHandler(state: ConversationState?, command: String?): HandlerHolder {
        fun findHandler(command: String): HandlerHolder? {
            for (entry in handlerMap) {
                if (command.startsWith(entry.key.command)) {
                    return HandlerHolder(entry.key, entry.value)
                }
            }
            return null
        }
        return if (state == null) {
            // Validate update argument (First call need to be command)
            if (command == null) {
                throw ConversationSessionException(chatId, "Command message expected")
            }
            logger.debug { "Empty state, trying to find handler for command: $command" }

            findHandler(command) ?: throw HandlerNotFound("Handler not found for command: $command")
        } else {
            HandlerHolder(state.handlerCommand, state.handler)
        }
    }

    private fun getCommandParams(command: String, params: LinkedList<String>): Map<String, String> {
        val commandWords = command.split(" ")
        if (params.isEmpty()) {
            return emptyMap()
        }
        val iterator = params.iterator()
        val commandParamMap = hashMapOf<String, String>()
        for (i in 1 until commandWords.size) {
            if (iterator.hasNext()) {
                val paramKey: String = iterator.next()
                val paramValue = commandWords[i]
                commandParamMap[paramKey] = paramValue
            } else {
                break
            }
        }
        return commandParamMap
    }

    private fun resetState() {
        finishCallback?.invoke()
        conversationState = null
    }

    private data class HandlerHolder(val handlerCommand: HandlerCommand, val handler: Handler)

}
