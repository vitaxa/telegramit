package org.botlaxy.telegramit.core.handler.loader.collect

import java.io.File
import java.nio.file.Path

interface ScriptCollector {
    fun collect(): List<Path>
}
