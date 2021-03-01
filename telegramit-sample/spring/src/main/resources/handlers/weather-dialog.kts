package handlers

import com.vitaxa.springtelegramitsample.weather.WeatherService
import org.botlaxy.telegramit.core.client.model.TelegramChatAction
import org.botlaxy.telegramit.core.client.model.TelegramChatActionRequest
import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.extension.emojize
import org.botlaxy.telegramit.core.request.TextMessage
import org.botlaxy.telegramit.spring.handler.dsl.springHandler
import java.util.concurrent.TimeUnit

springHandler("/weather", "weather") {

    val weatherService = getBean<WeatherService>()

    step<TelegramMessage>("country") {
        entry { _, _ ->
            TextMessage("What country are you interested in?")
        }
    }
    step<TelegramMessage>("city") {
        entry { _, _ ->
            TextMessage("What city are you interested?")
        }
    }
    process { ctx, args ->
        val chatId = ctx.message.chat.id
        ctx.telegram.sendMessage(TelegramChatRequest(chatId, "Give me a second..."))
        val telegramChatActionRequest = TelegramChatActionRequest(chatId, TelegramChatAction.TYPING)
        ctx.telegram.sendChatAction(telegramChatActionRequest)

        val country = (ctx.answer["country"] as TelegramMessage).text
        val city = (ctx.answer["city"] as TelegramMessage).text
        val weather = weatherService.getWeather(country ?: "", city ?: "")

        TextMessage("Today is ${weather.temp} temperature. ${weather.description}")
    }
}
