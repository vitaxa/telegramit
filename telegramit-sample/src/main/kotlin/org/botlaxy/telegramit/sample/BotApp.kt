import org.botlaxy.telegramit.core.bot
import org.botlaxy.telegramit.core.client.TelegramClientType
import org.botlaxy.telegramit.core.conversation.persistence.JacksonContextSerializer
import org.botlaxy.telegramit.core.conversation.persistence.MapDBConversationPersistence
import java.net.Proxy

fun main(args: Array<String>) {
    bot {
        name = "WeatherBot"
        token = "2416754356:ZCRTBs_wqGvGJNvfTzP7-3Rc3KDW1mQile3"
        proxy {
            type = Proxy.Type.SOCKS
            host = "177.61.50.104"
            port = 1080
            login = "vitaxa"
            password = "subprox"
        }
        handlerScriptConfig {
            handlerScriptPath = "telegramit/sample/handlers"
            handlerHotReload = true
        }
    }.start()
}
