package org.botlaxy.telegramit.core.client

import io.mockk.every
import io.mockk.mockkClass
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.connection.RealCall
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TelegramApiTest {

    lateinit var realCall: RealCall

    lateinit var okHttpClient: OkHttpClient

    lateinit var telegramApi: TelegramApi

    @Before
    fun setUp() {
        realCall = mockkClass(RealCall::class)
        okHttpClient = mockkClass(OkHttpClient::class)
        every { okHttpClient.newCall(any()) } returns realCall
        telegramApi = TelegramApi(okHttpClient, "testToken")
    }

    @Test
    fun `test sendMessage request`() {
        val sendMessageResponse = TelegramApiTest::class.java.getResource("/response/sendMessageResponse.json").readText()
        val response = buildResponse(sendMessageResponse)
        every { realCall.execute() } returns response
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
        val getUpdatesResponse = TelegramApiTest::class.java.getResource("/response/getUpdatesResponse.json").readText()
        val response = buildResponse(getUpdatesResponse)
        every { realCall.execute() } returns response
        val updates = telegramApi.getUpdates(0)
        Assert.assertTrue(updates.size == 1)
        Assert.assertTrue(updates[0].message != null)
    }

    private fun buildResponse(body: String, successful: Boolean = true): Response {
        val response = mockkClass(Response::class)
        every { response.isSuccessful } returns successful
        every { response.body } returns body.toResponseBody()

        return response
    }

}
