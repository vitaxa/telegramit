package handlers.weather

import org.botlaxy.telegramit.core.client.model.TelegramChatAction
import org.botlaxy.telegramit.core.client.model.TelegramChatActionRequest
import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.extension.emojize
import org.botlaxy.telegramit.core.handler.dsl.handler
import org.botlaxy.telegramit.core.request.TextMessage
import java.util.concurrent.TimeUnit

handler("/weather", "weather") {
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

        TimeUnit.SECONDS.sleep(2) // Long running task

        val country = (ctx.answer["country"] as TelegramMessage).text
        val city = (ctx.answer["city"] as TelegramMessage).text
        val resultMsg = "Your country ${ctx.answer["country"]} and city ${ctx.answer["city"]}."
        if (country.equals("Russia", ignoreCase = true) && city.equals("Moscow", ignoreCase = true)) {
            TextMessage("Today is 17 degrees Celsius")
        } else {
            TextMessage("Sorry i don't know :cry:".emojize())
        }
    }
}
