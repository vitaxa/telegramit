package org.botlaxy.telegramit.core.client

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.botlaxy.telegramit.core.Bot
import org.botlaxy.telegramit.core.client.api.TelegramApi
import java.nio.file.Paths
import java.security.KeyStore
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import kotlin.test.*

class TelegramWebhookClientTest {

    @MockK(relaxed = true)
    lateinit var updateListener: UpdateListener

    @MockK(relaxed = true)
    lateinit var telegramApi: TelegramApi

    lateinit var telegramWebhookClient: TelegramWebhookUpdateClient

    lateinit var httpClient: HttpClient

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        val certFileUri = TelegramWebhookClientTest::class.java.getResource("/cert/self-signed.jks").toURI()
        val certFile = Paths.get(certFileUri).toFile()
        val publicKeyUri = TelegramWebhookClientTest::class.java.getResource("/cert/self-signed.p12").toURI()
        val publicKeyFile = Paths.get(publicKeyUri).toFile()
        val pemUri = TelegramWebhookClientTest::class.java.getResource("/cert/self-signed.pem").toURI()
        val pemFile = Paths.get(pemUri).toFile()
        val clientConfig = mockk<Bot.TelegramWebhookClientConfig>() {
            every { host } returns "localhost"
            every { port } returns 7777
            every { keyStore } returns KeyStore.getInstance(certFile, "testPassword".toCharArray())
            every { keyAlias } returns "keytest"
            every { keyStoreFile } returns certFile
            every { keyStorePassword } returns "testPassword"
            every { privateKeyPassword } returns "testPassword"
            every { this@mockk.publicKeyFile } returns publicKeyFile
        }
        telegramWebhookClient = TelegramWebhookUpdateClient(telegramApi, updateListener, TEST_BOT_TOKEN, clientConfig)
        telegramWebhookClient.start()

        val heldCertificate = HeldCertificate.decode(pemFile.readBytes().decodeToString())
        val certificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(heldCertificate.certificate)
            .addPlatformTrustedCertificates()
            .build()
        val hostnameVerifier = HostnameVerifier { _, _ -> true }
        httpClient = HttpClient(OkHttp) {
            engine {
                config {
                    OkHttpClient.Builder()
                        .hostnameVerifier(hostnameVerifier)
                        .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager)
                        .build();
                }
            }
        }
    }

    @AfterTest
    fun tearDown() {
        telegramWebhookClient.close()
        httpClient.close()
    }

    @Test
    fun testBuildTelegramWebhookClient() {
        assertTrue { telegramWebhookClient != null }
    }

    @Ignore
    fun testTelegramWebhookClientHandle() {
        runBlocking {
            val response =
                httpClient.post<String> {
                    url("https://localhost:7777/hooker/${TEST_BOT_TOKEN.split(":")[1]}")
                    contentType(ContentType.Application.Json)
                    body = TelegramApiTest::class.java.getResource("/response/getUpdatesResponse.json").readText()
                }
            verify(exactly = 1) { updateListener.onUpdate(any()) }
            assertEquals("True", response)
        }
    }

    private companion object Constant {
        private const val TEST_BOT_TOKEN = "3122233760:AVFDtE_fgZOXuCmgQaR5-0Gs8NEQ5hAmrh8"
    }

}
