package org.botlaxy.telegramit.core.loader

import org.botlaxy.telegramit.core.handler.loader.collect.ClassPathScriptCollector
import org.botlaxy.telegramit.core.handler.loader.collect.DirectoryScriptCollector
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertTrue

class ScriptCollectorTest {

    @Test
    fun testDirectoryCollector() {
        val handlersDir = Paths.get("src", "test", "resources", "handlers")
        val directoryScriptCollector = DirectoryScriptCollector(handlersDir.toAbsolutePath().toString())
        val handlerScripts: List<Path> = directoryScriptCollector.collect()
        assertTrue { handlerScripts.size == 2 }
    }

    @Test
    fun testClasspathCollector() {
        val classPathScriptCollector = ClassPathScriptCollector()
        val handlerScripts = classPathScriptCollector.collect()
        assertTrue { handlerScripts.size == 2 }
    }

}
