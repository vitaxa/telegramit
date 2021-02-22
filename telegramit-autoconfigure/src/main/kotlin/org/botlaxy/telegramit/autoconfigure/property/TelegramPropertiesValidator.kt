package org.botlaxy.telegramit.autoconfigure.property

import org.botlaxy.telegramit.core.client.TelegramClientType
import org.springframework.validation.Errors
import org.springframework.validation.Validator

class TelegramPropertiesValidator : Validator {

    override fun supports(type: Class<*>): Boolean {
        return type === TelegramitProperties::class.java
    }

    override fun validate(target: Any, errors: Errors) {
        val properties = target as TelegramitProperties

        if (properties.token.isEmpty()) {
            errors.rejectValue("token", "token.null", "'token' must not be null!")
        }

        if (properties.mode == TelegramClientType.WEBHOOK) {
            val webhookHost = properties.webhookHost
            val webhookPort = properties.webHookPort
            if (webhookHost == null || webhookHost.isEmpty()) {
                errors.rejectValue(
                    "webhookHost", "webhookHost.empty",
                    "You have to set 'webhookHost' with Webhook mode."
                )
            }
            if (webhookPort == null) {
                errors.rejectValue("webhookPort", "webhookPort.null", "'webhookPort' must not be null!")
            }

        }
    }

}
