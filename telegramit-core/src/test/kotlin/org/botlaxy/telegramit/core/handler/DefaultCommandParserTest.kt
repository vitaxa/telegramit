package org.botlaxy.telegramit.core.handler

import org.junit.Assert
import org.junit.Test

class DefaultCommandParserTest {

    val commandParser = DefaultCommandParser()

    @Test
    fun `test command with params`() {
        val handlerCommand = commandParser.parse("/start <param1> <param2>")
        Assert.assertEquals("/start", handlerCommand.command)
        Assert.assertEquals("param1", handlerCommand.params.first)
        Assert.assertEquals("param2", handlerCommand.params.last)
    }

    @Test
    fun `test simple command`() {
        val handlerCommand = commandParser.parse("/start")
        Assert.assertEquals("/start", handlerCommand.command)
        Assert.assertTrue(handlerCommand.params.isEmpty())
    }

    @Test
    fun `test command without slash`() {
        val handlerCommand = commandParser.parse("start <param1>")
        Assert.assertEquals("start", handlerCommand.command)
        Assert.assertEquals("param1", handlerCommand.params.first)
    }

}
