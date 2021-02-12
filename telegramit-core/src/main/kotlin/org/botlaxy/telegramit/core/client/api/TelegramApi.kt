package org.botlaxy.telegramit.core.client.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.url
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.TelegramApiException
import org.botlaxy.telegramit.core.client.model.*
import org.botlaxy.telegramit.core.client.model.inline.InlineQueryAnswer
import java.io.File
import kotlin.collections.set

private val logger = KotlinLogging.logger {}

class TelegramApi(httpClient: HttpClient, accessKey: String) {

    private val jsonMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val rootUrl: String = "https://api.telegram.org/bot$accessKey"

    private val client: HttpClient = httpClient.config {
        HttpResponseValidator {
            validateResponse { response: HttpResponse ->
                val statusCode = response.status.value
                when (statusCode) {
                    in 500..599 -> throw ServerResponseException(response)
                }
            }
        }
    }

    fun getMe(): TelegramUser = runBlocking {
        val telegramResponse = client.get<TelegramResponse<TelegramUser>> { url("${rootUrl}/getMe") }

        processTelegramResponse(telegramResponse)
    }

    fun getUpdates(offset: Long?, limit: Int? = null, timeout: Int? = null): List<TelegramUpdate> = runBlocking {
        val params = hashMapOf<String, Any>()
        offset?.let { params["offset"] = it }
        limit?.let { params["limit"] = it }
        timeout?.let { params["timeout"] = it }
        val telegramResponse = client.post<TelegramResponse<List<TelegramUpdate>>> {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getUpdates")
            body = params
        }

        processTelegramResponse(telegramResponse)
    }

    fun setWebhook(url: String): Boolean = runBlocking {
        val reqBody = hashMapOf("url" to url)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/setWebhook")
            body = reqBody
        }

        processTelegramResponse(telegramResponse)
    }

    fun setWebhook(url: String, certificate: File): Boolean = runBlocking {
        val certByteArray = certificate.inputStream().readAllBytes()
        val reqBody = MultiPartFormDataContent(
            formData {
                append("url", url)
                if (certificate != null) {
                    appendInput(
                        key = "certificate",
                        size = certByteArray.size.toLong()
                    ) { buildPacket { writeFully(certByteArray) } }
                }
            }
        )
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            url("${rootUrl}/setWebhook")
            body = reqBody
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendMessage(message: TelegramChatRequest): TelegramMessage = runBlocking {
        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendMessage")
            body = message
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendChatAction(action: TelegramChatActionRequest): Boolean = runBlocking {
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendChatAction")
            body = action
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendPhoto(photoRequest: TelegramPhotoRequest): TelegramMessage = runBlocking {
        val formDataContent = createMediaFormData(photoRequest) {
            formData {
                appendInput(
                    key = "photo",
                    headers = Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=${photoRequest.photo.filename}"
                        )
                    },
                    size = photoRequest.photo.data.size.toLong()
                ) { buildPacket { writeFully(photoRequest.photo.data) } }
            }
        }

        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            url("${rootUrl}/sendPhoto")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendAudio(audioRequest: TelegramAudioRequest): TelegramMessage = runBlocking {
        val formDataContent = createMediaFormData(audioRequest) {
            appendInput(
                key = "audio",
                headers = Headers.build {
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=${audioRequest.audio.filename}"
                    )
                },
                size = audioRequest.audio.data.size.toLong()
            ) { buildPacket { writeFully(audioRequest.audio.data) } }
            audioRequest.duration?.let { append("duration", it.toString()) }
            audioRequest.performer?.let { append("performer", it.toString()) }
            audioRequest.title?.let { append("title", it.toString()) }
        }

        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            url("${rootUrl}/sendAudio")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendVoice(voiceRequest: TelegramVoiceRequest): TelegramMessage = runBlocking {
        val formDataContent = createMediaFormData(voiceRequest) {
            appendInput(
                key = "voice",
                headers = Headers.build {
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=${voiceRequest.voice.filename}"
                    )
                },
                size = voiceRequest.voice.data.size.toLong()
            ) { buildPacket { writeFully(voiceRequest.voice.data) } }
            voiceRequest.duration?.let { append("duration", it.toString()) }
        }

        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            url("${rootUrl}/sendVoice")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendDocument(documentRequest: TelegramDocumentRequest): TelegramMessage = runBlocking {
        val formDataContent = createMediaFormData(documentRequest) {
            appendInput(
                key = "document",
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "application/octet-stream")
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=${documentRequest.document.filename}"
                    )
                },
                size = documentRequest.document.data.size.toLong()
            ) { buildPacket { writeFully(documentRequest.document.data) } }
        }

        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            url("${rootUrl}/sendDocument")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendVideo(videoRequest: TelegramVideoRequest): TelegramMessage = runBlocking {
        val formDataContent = createMediaFormData(videoRequest) {
            formData {
                appendInput(
                    key = "video",
                    headers = Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=${videoRequest.video.filename}"
                        )
                    },
                    size = videoRequest.video.data.size.toLong()
                ) { buildPacket { writeFully(videoRequest.video.data) } }
                videoRequest.duration?.let { append("duration", it.toString()) }
                videoRequest.height?.let { append("height", it.toString()) }
                videoRequest.width?.let { append("width", it.toString()) }
            }
        }

        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            url("${rootUrl}/sendVideo")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendAnimation(animationRequest: TelegramAnimationRequest): TelegramMessage = runBlocking {
        val formDataContent = createMediaFormData(animationRequest) {
            formData {
                appendInput(
                    key = "animation",
                    headers = Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=${animationRequest.animation.filename}"
                        )
                    },
                    size = animationRequest.animation.data.size.toLong()
                ) { buildPacket { writeFully(animationRequest.animation.data) } }
                animationRequest.duration?.let { append("duration", it.toString()) }
                animationRequest.height?.let { append("height", it.toString()) }
                animationRequest.width?.let { append("width", it.toString()) }
            }
        }

        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            url("${rootUrl}/sendAnimation")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendVenue(venueRequest: TelegramVenueRequest): TelegramMessage = runBlocking {
        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendVenue")
            body = venueRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendContact(contactRequest: TelegramContactRequest): TelegramMessage = runBlocking {
        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendContact")
            body = contactRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendPoll(pollRequest: TelegramPollRequest): TelegramMessage = runBlocking {
        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendPoll")
            body = pollRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendDice(diceRequest: TelegramDiceRequest): TelegramMessage = runBlocking {
        val telegramResponse = client.post<TelegramResponse<TelegramMessage>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendDice")
            body = diceRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun kickChatMember(kickChatMemberRequest: TelegramKickChatMemberRequest): Boolean = runBlocking {
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/kickChatMember")
            body = kickChatMemberRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun unbanChatMember(unbanChatMemberRequest: TelegramUnbanChatMemberRequest): Boolean = runBlocking {
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/unbanChatMember")
            body = unbanChatMemberRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun restrictChatMember(restrictChatMemberRequest: TelegramRestrictChatMemberRequest): Boolean = runBlocking {
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/restrictChatMember")
            body = restrictChatMemberRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun promoteChatMemberRequest(promoteChatMemberRequest: TelegramPromoteChatMemberRequest): Boolean = runBlocking {
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/promoteChatMember")
            body = promoteChatMemberRequest
        }

        processTelegramResponse(telegramResponse)
    }

    fun setChatAdministratorCustomTitle(chatAdminCustomTitleRequest: TelegramChatAdminCustomTitleRequest): Boolean =
        runBlocking {
            val response = client.post<TelegramResponse<Boolean>>() {
                contentType(ContentType.Application.Json)
                url("${rootUrl}/setChatAdministratorCustomTitle")
                body = chatAdminCustomTitleRequest
            }

            processTelegramResponse(response)
        }

    fun setChatPermissions(chatPermissionRequest: TelegramChatPermissionRequest): Boolean = runBlocking {
        val response = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/setChatPermissions")
            body = chatPermissionRequest
        }

        processTelegramResponse(response)
    }

    fun exportChatInviteLink(chatId: Long): Boolean = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val response = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/exportChatInviteLink")
            body = param
        }

        processTelegramResponse(response)
    }

    fun setChatPhoto(chatId: Long, photo: ByteArray): Boolean = runBlocking {
        val formDataContent = MultiPartFormDataContent(
            formData {
                append("chat_id", chatId.toString())
                appendInput(
                    key = "photo",
                    size = photo.size.toLong()
                ) { buildPacket { writeFully(photo) } }
            }
        )
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            url("${rootUrl}/setChatPhoto")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun deleteChatPhoto(chatId: Long): Boolean = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/deleteChatPhoto")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun setChatTitle(chatId: Long, title: String): Boolean = runBlocking {
        val param = mapOf("chat_id" to chatId, "title" to title)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/setChatTitle")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun setChatDescription(chatId: Long, description: String): Boolean = runBlocking {
        val param = mapOf("chat_id" to chatId, "description" to description)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/setChatDescription")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun pinChatMessage(chatId: Long, messageId: Int, disableNotification: Boolean = false): Boolean = runBlocking {
        val param = mapOf(
            "chat_id" to chatId,
            "message_id" to messageId,
            "disable_notification" to disableNotification
        )
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/pinChatMessage")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun unpinChatMessage(chatId: Long): Boolean = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/unpinChatMessage")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun leaveChat(chatId: Long): Boolean = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/leaveChat")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun getChat(chatId: Long): TelegramChat = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val telegramResponse = client.post<TelegramResponse<TelegramChat>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getChat")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun getChatAdministrators(chatId: Long): List<TelegramChatMember> = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val telegramResponse = client.post<TelegramResponse<List<TelegramChatMember>>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getChatAdministrators")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun getChatMembersCount(chatId: Long): Int = runBlocking {
        val param = mapOf("chat_id" to chatId)
        val telegramResponse = client.post<TelegramResponse<Int>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getChatMembersCount")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun getChatMember(chatId: Long, userId: Int): TelegramChatMember = runBlocking {
        val param = mapOf("chat_id" to chatId, "user_id" to userId)
        val telegramResponse = client.post<TelegramResponse<TelegramChatMember>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getChatMember")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun setMyCommands(commands: List<TelegramBotCommand>): Boolean = runBlocking {
        val param = mapOf("commands" to commands)
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/setMyCommands")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun getMyCommands(): List<TelegramBotCommand> = runBlocking {
        val telegramResponse = client.get<TelegramResponse<List<TelegramBotCommand>>> {
            url("${rootUrl}/getMyCommands")
        }

        processTelegramResponse(telegramResponse)
    }

    fun getFile(fileId: String): TelegramFile = runBlocking {
        val param = mapOf("file_id" to fileId)
        val telegramResponse = client.post<TelegramResponse<TelegramFile>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendDice")
            body = param
        }

        processTelegramResponse(telegramResponse)
    }

    fun answerInlineQuery(inlineQueryAnswer: InlineQueryAnswer): Boolean = runBlocking {
        val telegramResponse = client.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/answerInlineQuery")
            body = inlineQueryAnswer
        }

        processTelegramResponse(telegramResponse)
    }

    private inline fun <reified T> processTelegramResponse(telegramResponse: TelegramResponse<T>): T {
        if (!telegramResponse.ok) {
            throw TelegramApiException("Description: '${telegramResponse.description}'. Code: ${telegramResponse.errorCode}")
        }
        return telegramResponse.result!!
    }

    private fun createMediaFormData(
        mediaRequest: TelegramMediaRequest,
        block: FormBuilder.() -> Unit
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append("chat_id", mediaRequest.chatId.toString())
                apply(block)
                append("disable_notification", mediaRequest.disableNotification.toString())
                mediaRequest.caption?.let { append("caption", it) }
                mediaRequest.replyKeyboard?.let {
                    val jsonKb = jsonMapper.writeValueAsString(it)
                    append("reply_markup", jsonKb)
                }
                mediaRequest.parseMode?.let { append("parse_mode", mediaRequest.parseMode.toString()) }
            }
        )
    }

    companion object {
        const val JSON_CONTENT_TYPE: String = "application/json"
        const val MULTIPART_CONTENT_TYPE: String = "multipart/form-data"
    }

}
