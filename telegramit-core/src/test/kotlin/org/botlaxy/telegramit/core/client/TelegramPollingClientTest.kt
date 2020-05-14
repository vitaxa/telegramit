package org.botlaxy.telegramit.core.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.botlaxy.telegramit.core.Bot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramResponse
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TelegramPollingClientTest {

    @MockK(relaxed = true)
    lateinit var updateListener: UpdateListener

    @MockK(relaxed = true)
    lateinit var telegramApi: TelegramApi

    @MockK(relaxed = true)
    lateinit var clientConfig: Bot.TelegramPoolingClientConfig

    lateinit var telegramPollingClient: TelegramPollingClient

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        val updatesResponse = TelegramApiTest::class.java.getResource("/response/getUpdatesResponse.json").readText()
        val telegramUpdates = jacksonObjectMapper().readValue<TelegramResponse<List<TelegramUpdate>>>(updatesResponse)
        every { telegramApi.getUpdates(any(), any(), any()) } returns telegramUpdates.result!!
        telegramPollingClient = TelegramPollingClient(telegramApi, updateListener, clientConfig)
    }

    @Test
    fun testTelegramPollingClientHandle() {
        telegramPollingClient.start()
        verify(timeout = 5000, atLeast = 1) { updateListener.onUpdate(any()) }
        telegramPollingClient.close()
    }

}
