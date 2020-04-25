package org.botlaxy.telegramit.core.handler

import java.util.*

data class HandlerCommand(val command: String, val params: LinkedList<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HandlerCommand

        if (command != other.command) return false

        return true
    }

    override fun hashCode(): Int {
        return command.hashCode()
    }
}
