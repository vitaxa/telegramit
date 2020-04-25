package org.botlaxy.telegramit.core.handler

import java.io.StringReader
import java.util.*

class DefaultCommandParser : CommandParser {

    private var ch = -1

    private var reader: StringReader? = null

    override fun parse(command: String): HandlerCommand {
        if (reader == null) {
            reader = StringReader(command)
        }
        try {
            val parsedCommand = readCommand() // Read first word as command
            val commandParams = LinkedList<String>() // Read all command params
            while (nextChar() != -1) {
                val param = readParam()
                param?.let { commandParams.add(it) }
            }

            return HandlerCommand(parsedCommand, commandParams)
        } finally {
            reader?.close()
            reader = null
        }
    }

    private fun readCommand(): String {
        nextChar(true)
        val sb = StringBuilder()
        while (!Character.isWhitespace(ch) && ch != -1) {
            sb.append(ch.toChar())
            nextChar()
        }
        return sb.toString()
    }

    private fun readParam(): String? {
        val char = ch.toChar()
        if (char == '<') {
            nextChar(true)
            val sb = StringBuilder()
            while (ch.toChar() != '>') {
                sb.append(ch.toChar())
                nextChar(true)
            }
            return sb.toString()
        }
        return null
    }

    private fun nextChar(eof: Boolean = false): Int {
        ch = reader!!.read()
        if (eof && ch < 0) {
            throw CommandParserException("Unexpected end of char")
        }
        return ch
    }

}
