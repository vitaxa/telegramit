package org.botlaxy.telegramit.core.handler.loader.collect

import java.nio.file.Path

interface ScriptCollector {
    fun collect(): List<Path>
}
