package org.botlaxy.telegramit.core.client

import mu.KotlinLogging
import org.botlaxy.telegramit.core.Bot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import org.botlaxy.telegramit.core.client.model.TelegramUpdate
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

private val logger = KotlinLogging.logger {}

class TelegramPollingClient(
    private val telegramApi: TelegramApi,
    private val updateListener: UpdateListener,
    val config: Bot.TelegramPoolingClientConfig
) : TelegramClient {

    private val timeout = config.timeoutInSec ?: DEFAULT_POOLING_TIMEOUT

    private val limit = config.limit ?: DEFAULT_POOLING_LIMIT

    @Volatile
    private var running = false

    private val poolingClient: AtomicReference<PoolingClient> = AtomicReference()

    override fun start() {
        logger.debug { "Start Telegram pooling client" }
        if (running || !poolingClient.compareAndSet(null, PoolingClient())) {
            throw IllegalStateException("Telegram pooling client already started")
        }
        running = true
        val poolingClient = poolingClient.get()
        Thread(poolingClient, "TelegramPoolingClient").start()
    }

    override fun onUpdate(update: TelegramUpdate) {
        logger.debug { "Got a new update event: $update" }
        updateListener.onUpdate(update)
    }

    override fun close() {
        logger.debug { "Close telegram pooling client" }
        running = false
        poolingClient.set(null)
    }

    private inner class PoolingClient : Runnable {

        private var offset = 0L

        override fun run() {
            while (running) {
                try {
                    val updates = telegramApi.getUpdates(offset, limit, timeout)
                    if (updates.isEmpty()) {
                        continue
                    }
                    for (update in updates) {
                        onUpdate(update)
                    }
                    offset = updates.last().id + 1
                } catch (e: Exception) {
                    logger.error(e) { "Unexpected exception during update pooling" }
                    TimeUnit.SECONDS.sleep(3)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_POOLING_TIMEOUT = 10
        private const val DEFAULT_POOLING_LIMIT = 100
    }

}
