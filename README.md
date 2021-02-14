![Telegramit](doc/telegramit-logo.png)

Telegramit is an attempt at creating a convenient framework that includes key functionality for developing Telegram chat bots.

> Notice! Please leave an issue if some important feature is missing in the current realisation: I will add it as soon as possible.

## Features:

- Conversation persistence (in case of application restarts)

- Convenient conversation scenario description via Kotlin DSL

- Hot reload for DSL scripts

- Extremely simple configuration and deployment

- Inline mode

## Setup:

```
repositories {
    maven {
        setUrl("https://dl.bintray.com/vitaxa/telegramit/")
    }
}
```

Gradle:

`implementation("org.botlaxy:telegramit-core:0.1.21")`

Maven:
```
<dependency>
    <groupId>org.botlaxy</groupId>
    <artifactId>telegramit-core</artifactId>
    <version>0.1.21</version>
</dependency>
```

## Getting Started:
```Kotlin
bot {
    name = "WeatherBot"
    token = "2416754356:ZCRTBs_wqGvGJNvfTzP7-3Rc3KDW1mQile3"
}.start()
```
Both fields are required, a `token` for your bot can be acquired from [BotFather](#BotFather "https://tele.gs/botfather"). 

Deploying a bot using a proxy:
```Kotlin
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

The logic of a bot is described using DSL constructions (Kotlin DSL). There are two ways of adding it:

either place your `.kts` script into the resource folder: `resources/handlers/SimpleHandler.kts`;

or add any path in your system. This choice supports hot reaload in the production environment:
```Kotlin
handlerScriptConfig {
    handlerScriptPath = "telegramit/sample/handlers"
    handlerHotReload = true
}
```
If you need the bot to persist its current conversation, this configuration is required:
```Kotlin
persistenceConfig {
    conversationPersistence = MapDBConversationPersistence(JacksonContextSerializer())
}
```
This is the default configuration but you are free to change it.

Persistence is currently used ONLY for the conversation context. 

The `Polling` method is used by default for interacting with the Telegram API. Use this configuration if you need to change to `Webhook`: 
```Kotlin
client {
    type = TelegramClientType.WEBHOOK
    host = "your_host"
    port = 9000
}
```

You can also customise some characteristics of the `Polling` method:
```Kotlin
client {
    type = TelegramClientType.POOLING
    limit = 10
    timeout = 5
}
```

#### How to handle user message:
To process message create a `kts` file in the `resources` directory with `handler` function

More about using [handler](/telegramit-sample/README.md)

#### Inline mode:
To use inline mode, you need to specify the command `/setinline` using [BotFather](https://tele.gs/botfather) select the bot for which this mode will be activated

After activating inline mode, you can start processing messages of this type, for this purpose create a new `kts` file in the `resources` directory with `inlineHandler` function

> only one inlineHandler can exist

More about using [inlineHandler](/telegramit-sample/README.md)

> The directory for kts files can be changed via `handlerScriptConfig`

## TODO:

- Full test coverage

## Thanks to:
Special thanks to [Telegraff](https://github.com/ruslanys/telegraff) for the inspiration and some code base. In addition, pay your attention to this library, if you use **Spring**. 




