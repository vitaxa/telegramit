package handlers.weather

import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.botlaxy.telegramit.core.extension.emojize
import org.botlaxy.telegramit.core.handler.dsl.handler
import org.botlaxy.telegramit.core.request.TextMessage

handler("/w <country_arg> <city_arg>") {
    process { ctx, args ->
        val country = args["country_arg"]
        val city = args["city_arg"]
        if (country == null || city == null) {
            TextMessage("Please type correct params")
        }
        if (country.equals("Russia", ignoreCase = true) && city.equals("Moscow", ignoreCase = true)) {
            TextMessage("Your country $country and city $city. Today is 17 degrees Celsius")
        } else {
            TextMessage("Sorry i don't know :cry:".emojize())
        }
    }
}
