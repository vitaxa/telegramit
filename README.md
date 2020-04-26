![Telegramit](doc/telegramit-logo.png)

Telegramit is an attempt to make a convenient framework, involving the main functional for Telegram chat bot development.

Notice! Please, leave your issues, if you don’t have something important in this realization, and I’ll add this soon.

## Features:

- Saving of the current conversation (in a case of application’s restart)

- Comfortable description of the script of the conversation using Kotlin DSL

- Hot reload for DSL scripts

- The simplest configuration and Bot’s launch

## Setup:

```
repositories {
    maven {
        setUrl("https://dl.bintray.com/vitaxa/telegramit/")
    }
}
```

Gradle:

`implementation("org.botlaxy:telegramit-core:0.0.21")`

Maven:
```
<dependency>
    <groupId>org.botlaxy</groupId>
    <artifactId>telegramit-core</artifactId>
    <version>0.0.21</version>
</dependency>
```

## Getting Started:
```
bot {
    name = "WeatherBot"
    token = "2416754356:ZCRTBs_wqGvGJNvfTzP7-3Rc3KDW1mQile3"
}.start()
```
Both of the field are required, you can get Bot’s token from [BotFather](#BotFather "https://tele.gs/botfather"). 

The Bot’s launch with proxy:
```
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
}.start()
```

The logic of Bot is described with DSL constructions (Kotlin DSL). You have two ways, how you could place them. 

The first one is when you place kts script into the resource folder: resources/handlers/SimpleHandler.kts. 

And the second one is any path in your system. By the way, this choice supports HotReload in the production environment:
```
handlerScriptConfig { 
    handlerScriptPath = "telegramit/sample/handlers"
    handlerHotReload = true
}
```
Specify next configuration after the reloading, if you need the Bot to remember the current conversation:
```
persistenceConfig {
    conversationPersistence = MapDBConversationPersistence(JacksonContextSerializer())
}
```
This is a default configuration, you can write your own, if you wish. 

Preservation is using ONLY for conversation saving at the moment. 

The Poling method is used by default for the cooperation with the Telegram API. If you need to change to the Webhook: 
```
client { 
    type = TelegramClientType.WEBHOOK
    host = "botlaxy.org"
    port = 9000
}
```

Besides, there is an opportunity for customizing some characteristic (https://core.telegram.org/bots/api#getupdates) of the Poling method:
```
client {
    type = TelegramClientType.POOLING
    limit = 10
    timeout = 5
}
```

## TODO:
- Full support of the Telegram API

- Inline mode

- Improvement of the functioning at the Webhook client

- Full test coverage

## Thanks to:
Special thanks to [Telegraff](#Telegraff "https://github.com/ruslanys/telegraff") for the inspiration and some code base. In addition, pay your attention to this library, if you use **Spring**. 




