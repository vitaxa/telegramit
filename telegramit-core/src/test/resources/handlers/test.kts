import org.botlaxy.telegramit.core.client.model.TelegramChatRequest
import org.botlaxy.telegramit.core.client.model.TelegramMessage
import org.botlaxy.telegramit.core.extension.emojize
import org.botlaxy.telegramit.core.handler.dsl.handler
import org.botlaxy.telegramit.core.request.TextMessage
import org.botlaxy.telegramit.core.request.keyboard
import java.util.concurrent.TimeUnit

handler("/test") {
    process { ctx, _ ->
        TextMessage("Test")
    }
}
