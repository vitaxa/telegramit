package org.botlaxy.telegramit.autoconfigure.property

import org.botlaxy.telegramit.core.client.TelegramClientType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.net.Proxy

@Component
@ConfigurationProperties(prefix = "telegramit", ignoreUnknownFields = false)
class TelegramitProperties {

    var name: String = "Telegramit"

    var token: String = ""

    var mode = TelegramClientType.POLLING

    var webhookHost: String? = null

    var webHookPort: Int? = null

    var proxyType: Proxy.Type? = null

    var proxyHost: String? = null

    var proxyPort: Int? = null

    var proxyLogin: String? = null

    var proxyPassword: String? = null



}
