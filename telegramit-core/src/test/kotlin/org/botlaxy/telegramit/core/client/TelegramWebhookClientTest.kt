package org.botlaxy.telegramit.core.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.botlaxy.telegramit.core.Bot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramWebhookClientTest {

    @MockK(relaxed = true)
    lateinit var updateListener: UpdateListener

    @MockK(relaxed = true)
    lateinit var telegramApi: TelegramApi

    lateinit var telegramWebhookClient: TelegramWebhookClient

    lateinit var httpClient: HttpClient

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        val clientConfig = mockk<Bot.TelegramWebhookClientConfig>() {
            every { host } returns "localhost"
            every { port } returns 7777
        }
        telegramWebhookClient = TelegramWebhookClient(telegramApi, updateListener, TEST_BOT_TOKEN, clientConfig)
        telegramWebhookClient.start()
        httpClient = HttpClient(OkHttp)
    }

    @AfterTest
    fun tearDown() {
        telegramWebhookClient.close()
        httpClient.close()
    }

    @Test
    fun testTelegramWebhookClientHandle() {
        runBlocking {
            val response =
                httpClient.post<String> {
                    url("http://localhost:7777/hooker/$TEST_BOT_TOKEN")
                    contentType(ContentType.Application.Json)
                    body = TelegramApiTest::class.java.getResource("/response/getUpdatesResponse.json").readText()
                }
            verify(exactly = 1) { updateListener.onUpdate(any()) }
            assertEquals("True", response)
        }
    }

    private companion object Constant {
        private const val TEST_BOT_TOKEN = "testToken"
    }

}
