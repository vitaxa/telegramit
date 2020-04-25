package org.botlaxy.telegramit.core.handler

interface CommandParser {
    fun parse(command: String): HandlerCommand
}
