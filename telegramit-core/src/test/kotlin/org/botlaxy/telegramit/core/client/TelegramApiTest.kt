package org.botlaxy.telegramit.core.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.*
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TelegramApiTest {

    lateinit var telegramApi: TelegramApi

    @Before
    fun setUp() {
        val httpClient = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.url.fullUrl) {
                        "${API_URL}${TEST_TOKEN}/getUpdates" -> {
                            val response = TelegramApiTest::class.java.getResource("/response/getUpdatesResponse.json")
                                .readText(Charsets.UTF_8)
                            respond(response, headers = responseHeaders)
                        }
                        "${API_URL}${TEST_TOKEN}/sendMessage" -> {
                            val response = TelegramApiTest::class.java.getResource("/response/sendMessageResponse.json")
                                    .readText(Charsets.UTF_8)
                            respond(response, headers = responseHeaders)
                        }
                        else -> error("Unhandled ${request.url.fullPath}")
                    }
                }
            }
        }
        telegramApi = TelegramApi(httpClient, TEST_TOKEN)
    }

    @Test
    fun `test sendMessage request`() {
        val telegramMessage = telegramApi.sendMessage(TelegramChatRequest(0L, "test"))
        Assert.assertEquals(484, telegramMessage.id)
        Assert.assertEquals(1142232760L, telegramMessage.user?.id)
        Assert.assertEquals(true, telegramMessage.user?.isBot)
        Assert.assertEquals("Subscriptor", telegramMessage.user?.firstName)
        Assert.assertEquals("JoeBridgesBot", telegramMessage.user?.username)
        Assert.assertEquals(506658066L, telegramMessage.chat.id)
        Assert.assertEquals("Vitaxa", telegramMessage.chat.firstName)
        Assert.assertEquals("Vitaxa64", telegramMessage.chat.username)
        Assert.assertEquals("private", telegramMessage.chat.type)
        Assert.assertEquals(1585583727L, telegramMessage.date)
        Assert.assertEquals("Just some message", telegramMessage.text)
    }

    @Test
    fun `test getUpdates request`() {
        val updates = telegramApi.getUpdates(0)
        Assert.assertTrue(updates.size == 1)
        Assert.assertTrue(updates[0].message != null)
    }

    private companion object {
        const val TEST_TOKEN = "4f31dg5rd"
        const val API_URL = "https://api.telegram.org/bot"
    }

    private val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
    private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
    private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

}
