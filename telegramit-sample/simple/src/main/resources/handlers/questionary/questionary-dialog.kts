import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.extension.emojize
import org.botlaxy.telegramit.core.handler.dsl.handler
import org.botlaxy.telegramit.core.request.TextMessage
import org.botlaxy.telegramit.core.request.keyboard
import java.util.concurrent.TimeUnit

handler("/questionary") {
    step<String>("fio") {
        entry { _, _ ->
            TextMessage("Write your name")
        }
        validation { msg ->
            if (msg.text == null) {
                TextMessage("I asked for name :angry:".emojize())
            } else {
                null
            }
        }
        resolver { msg ->
            msg.text!!
        }
    }
    step<TelegramMessage>("age") {
        entry { _, _ ->
            TextMessage("How old are you?")
        }
    }
    step<String>("rate") {
        entry { _, _ ->
            val rateKeyboard = keyboard {
                row {
                    button("Yes")
                    button("No")
                }
            }
            TextMessage("Did you like a questionary?", replyKeyboard = rateKeyboard)
        }
    }
    process { ctx, _ ->
        val fio = ctx.answer["fio"] as String
        val age = ctx.answer["age"] as TelegramMessage
        val rate = ctx.answer["rate"] as TelegramMessage

        // Save questionary to the database
        TimeUnit.SECONDS.sleep(1)

        TextMessage("Thank you for you answers")
    }
}
