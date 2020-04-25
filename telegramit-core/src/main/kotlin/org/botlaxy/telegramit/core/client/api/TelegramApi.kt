package org.botlaxy.telegramit.core.client.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.botlaxy.telegramit.core.client.TelegramApiException
import org.botlaxy.telegramit.core.client.model.*

private val logger = KotlinLogging.logger {}

class TelegramApi(private val httpClient: OkHttpClient, accessKey: String) {

    private val jsonMapper: ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val rootUrl: String = "https://api.telegram.org/bot$accessKey/"

    fun getUpdates(offset: Long?, limit: Int? = null, timeout: Int? = null): List<TelegramUpdate> {
        val params = hashMapOf<String, Any>()
        offset?.let { params["offset"] = it }
        limit?.let { params["limit"] = it }
        timeout?.let { params["timeout"] = it }
        val json = jsonMapper.writeValueAsString(params)
        val body: RequestBody = json.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(rootUrl + "getUpdates")
            .post(body)
            .addHeader("Content-Type", JSON_MEDIA_TYPE.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<List<TelegramUpdate>>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun setWebhook(url: String): Boolean {
        val params = hashMapOf("url" to url)
        val json = jsonMapper.writeValueAsString(params)
        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(rootUrl + "setWebhook")
            .post(body)
            .addHeader("Content-Type", JSON_MEDIA_TYPE.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<Boolean>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendMessage(message: TelegramChatRequest): TelegramMessage {
        val json = jsonMapper.writeValueAsString(message)
        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(rootUrl + "sendMessage")
            .post(body)
            .addHeader("Content-Type", JSON_MEDIA_TYPE.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramMessage>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendChatAction(action: TelegramChatActionRequest): Boolean {
        val json = jsonMapper.writeValueAsString(action)
        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(rootUrl + "sendChatAction")
            .post(body)
            .addHeader("Content-Type", JSON_MEDIA_TYPE.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<Boolean>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendPhoto(photoRequest: TelegramPhotoRequest): TelegramMessage {
        val mediaFormData = createMediaFormData(photoRequest).apply {
            addFormDataPart("photo", "upload_photo.png", photoRequest.photo.toRequestBody())
        }
        val multipartBody = mediaFormData.build()
        val request = Request.Builder()
            .url(rootUrl + "sendPhoto")
            .post(multipartBody)
            .addHeader("Content-Type", MULTIPART_FORM_DATA.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramMessage>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendAudio(audioRequest: TelegramAudioRequest): TelegramMessage {
        val mediaFormData = createMediaFormData(audioRequest).apply {
            addFormDataPart("audio", audioRequest.audio.filename, audioRequest.audio.audio.toRequestBody())
            audioRequest.duration?.let { addFormDataPart("duration", it.toString()) }
            audioRequest.performer?.let { addFormDataPart("performer", it.toString()) }
            audioRequest.title?.let { addFormDataPart("title", it.toString()) }
        }
        val multipartBody = mediaFormData.build()
        val request = Request.Builder()
            .url(rootUrl + "sendAudio")
            .post(multipartBody)
            .addHeader("Content-Type", MULTIPART_FORM_DATA.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramMessage>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendVoice(voiceRequest: TelegramVoiceRequest): TelegramMessage {
        val mediaFormData = createMediaFormData(voiceRequest).apply {
            addFormDataPart("voice", voiceRequest.voice.filename, voiceRequest.voice.voice.toRequestBody())
            voiceRequest.duration?.let { addFormDataPart("duration", it.toString()) }
        }
        val multipartBody = mediaFormData.build()
        val request = Request.Builder()
            .url(rootUrl + "sendVoice")
            .post(multipartBody)
            .addHeader("Content-Type", MULTIPART_FORM_DATA.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramMessage>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendDocument(documentRequest: TelegramDocumentRequest): TelegramMessage {
        val mediaFormData = createMediaFormData(documentRequest).apply {
            addFormDataPart(
                "document",
                documentRequest.document.fileName,
                documentRequest.document.document.toRequestBody()
            )
        }
        val multipartBody = mediaFormData.build()
        val request = Request.Builder()
            .url(rootUrl + "sendDocument")
            .post(multipartBody)
            .addHeader("Content-Type", MULTIPART_FORM_DATA.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramMessage>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun sendVideo(videoRequest: TelegramVideoRequest): TelegramMessage {
        val mediaFormData = createMediaFormData(videoRequest).apply {
            addFormDataPart("video", videoRequest.video.filename, videoRequest.video.video.toRequestBody())
            videoRequest.duration?.let { addFormDataPart("duration", it.toString()) }
            videoRequest.height?.let { addFormDataPart("height", it.toString()) }
            videoRequest.width?.let { addFormDataPart("width", it.toString()) }
        }
        val multipartBody = mediaFormData.build()
        val request = Request.Builder()
            .url(rootUrl + "sendVideo")
            .post(multipartBody)
            .addHeader("Content-Type", MULTIPART_FORM_DATA.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramMessage>>(request)

        return processTelegramResponse(telegramResponse)
    }

    fun getFile(fileId: String): TelegramFile {
        val json = jsonMapper.writeValueAsString(fileId)
        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(rootUrl + "getFile")
            .post(body)
            .addHeader("Content-Type", JSON_MEDIA_TYPE.toString())
            .build()
        val telegramResponse = sendRequest<TelegramResponse<TelegramFile>>(request)

        return processTelegramResponse(telegramResponse)
    }

    private inline fun <reified T> sendRequest(request: Request): T {
        logger.debug { "Send telegram request: $request" }
        val response: Response = httpClient.newCall(request).execute()
        logger.debug { "Received telegram response: $response" }
        if (!response.isSuccessful) {
            throw TelegramApiException("Bad response: $response. Body: ${response.body?.string()}")
        }
        val body = response.body!!.string()
        logger.debug { "Telegram response body: $body" }

        return jsonMapper.readValue<T>(body)
    }

    private inline fun <reified T> processTelegramResponse(telegramResponse: TelegramResponse<T>): T {
        if (!telegramResponse.ok) {
            throw TelegramApiException("${telegramResponse.description}-${telegramResponse.errorCode}")
        }
        return telegramResponse.result!!
    }

    private fun createMediaFormData(mediaRequest: TelegramMediaRequest): MultipartBody.Builder {
        return MultipartBody.Builder().apply {
            setType(MULTIPART_FORM_DATA)
            addFormDataPart("chat_id", mediaRequest.chatId.toString())
            addFormDataPart("disable_notification", mediaRequest.disableNotification.toString())
            mediaRequest.caption?.let { addFormDataPart("caption", it) }
            mediaRequest.replyKeyboard?.let {
                val jsonKb = jsonMapper.writeValueAsString(it)
                addFormDataPart("reply_markup", jsonKb)
            }
            mediaRequest.parseMode?.let { addFormDataPart("parse_mode", mediaRequest.parseMode.toString()) }
        }
    }

    companion object {
        val JSON_MEDIA_TYPE: MediaType = "application/json".toMediaType()
        val MULTIPART_FORM_DATA: MediaType = "multipart/form-data".toMediaType()
    }

}
