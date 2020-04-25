package org.botlaxy.telegramit.core.handler.loader

import org.botlaxy.telegramit.core.handler.dsl.Handler

interface ScriptUpdateListener {
    fun onUpdate(
        oldHandler: Handler?,
        newHandler: Handler
    )
}
