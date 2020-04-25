package org.botlaxy.telegramit.core.conversation

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.conversation.persistence.ConversationPersistence
import org.botlaxy.telegramit.core.handler.dsl.Handler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class ConversationManagerTest {

    @MockK
    lateinit var telegramApi: TelegramApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    private val handlerMock: Handler = mockk<Handler>() {
        every { commands } returns listOf(
            mockk() {
                every { command } returns "/start"
            },
            mockk() {
                every { command } returns "/test"
                every { params } returns LinkedList(listOf("param1"))
            }
        )
    }

    @Test
    fun `test new conversation creating`() {
        val conversationManager = ConversationManager(
            telegramApi,
            listOf(handlerMock)
        )
        val conversationSession = conversationManager.getConversation(4324325)
        Assert.assertEquals(4324325, conversationSession.chatId)
    }

    @Test
    fun `test conversation save`() {
        val conversationManager = ConversationManager(
            telegramApi,
            listOf(handlerMock)
        )
        val conversationSession = conversationManager.getConversation(6536335)
        val oldConversationSession = conversationManager.getConversation(6536335)
        Assert.assertTrue(conversationSession == oldConversationSession)
    }

    @Test
    fun `test conversation closing`() {
        val conversationManager = ConversationManager(
            telegramApi,
            listOf(handlerMock)
        )
        val conversationSession = conversationManager.getConversation(74563)
        conversationManager.closeConversation(74563)
        val newConversationSession = conversationManager.getConversation(74563)
        Assert.assertTrue(conversationSession != newConversationSession)
    }

    @Test
    fun `test persistence call`() {
        val conversationPersistence = mockk<ConversationPersistence>(relaxed = true)
        val conversationManager = ConversationManager(
            telegramApi,
            listOf(handlerMock),
            conversationPersistence
        )
        conversationManager.closeConversation(12345)
        verify(exactly = 1) { conversationPersistence.deleteConversation(any()) }
        conversationManager.clearAllConversation()
        verify(exactly = 1) { conversationPersistence.clearConversations() }
    }

}
