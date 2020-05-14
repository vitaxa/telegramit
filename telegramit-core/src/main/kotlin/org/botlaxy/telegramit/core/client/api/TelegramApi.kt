package org.botlaxy.telegramit.core.client.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.botlaxy.telegramit.core.client.TelegramApiException
import org.botlaxy.telegramit.core.client.model.*
import java.io.ByteArrayInputStream
import kotlin.collections.set

private val logger = KotlinLogging.logger {}

class TelegramApi(private val httpClient: HttpClient, accessKey: String) {

    private val jsonMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val rootUrl: String = "https://api.telegram.org/bot$accessKey"

    fun getUpdates(offset: Long?, limit: Int? = null, timeout: Int? = null): List<TelegramUpdate> = runBlocking {
        val params = hashMapOf<String, Any>()
        offset?.let { params["offset"] = it }
        limit?.let { params["limit"] = it }
        timeout?.let { params["timeout"] = it }
        val telegramResponse = httpClient.post<TelegramResponse<List<TelegramUpdate>>> {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getUpdates")
            body = params
        }

        processTelegramResponse(telegramResponse)
    }

    fun setWebhook(url: String): Boolean = runBlocking {
        val params = hashMapOf("url" to url)
        val telegramResponse = httpClient.post<TelegramResponse<Boolean>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/setWebhook")
            body = params
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendMessage(message: TelegramChatRequest): TelegramMessage = runBlocking {
        val telegramResponse = httpClient.post<TelegramResponse<TelegramMessage>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/sendMessage")
            body = message
        }

        processTelegramResponse(telegramResponse)
    }

    fun sendChatAction(action: TelegramChatActionRequest): Boolean = runBlocking {
        val telegramResponse = httpClient.post<TelegramResponse<Boolean>>() {
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

        val telegramResponse = httpClient.post<TelegramResponse<TelegramMessage>>() {
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

        val telegramResponse = httpClient.post<TelegramResponse<TelegramMessage>>() {
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

        val telegramResponse = httpClient.post<TelegramResponse<TelegramMessage>>() {
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

        val telegramResponse = httpClient.post<TelegramResponse<TelegramMessage>>() {
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

        val telegramResponse = httpClient.post<TelegramResponse<TelegramMessage>>() {
            headers.append("Content-Type", MULTIPART_CONTENT_TYPE);
            url("${rootUrl}/sendVideo")
            body = formDataContent
        }

        processTelegramResponse(telegramResponse)
    }

    fun getFile(fileId: String): TelegramFile = runBlocking {
        val param = mapOf("file_id" to fileId)
        val telegramResponse = httpClient.post<TelegramResponse<TelegramFile>>() {
            contentType(ContentType.Application.Json)
            url("${rootUrl}/getFile")
            body = param
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
