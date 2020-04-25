package org.botlaxy.telegramit.core.extension

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchService
import java.security.MessageDigest
import kotlin.experimental.and

fun Path.createParentDirs() {
    val parent: Path = parent
    if (!Files.isDirectory(this)) {
        Files.createDirectories(parent)
    }
}

fun Path.watch(vararg watchEventKinds: WatchEvent.Kind<*>): WatchService {
    val watchService = this.fileSystem.newWatchService()
    register(watchService, watchEventKinds)

    return watchService
}

fun Path.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    md.update(Files.readAllBytes(this))
    val digest = md.digest()
    val sb = StringBuilder()
    for (element in digest) {
        sb.append(((element and 0xff.toByte()) + 0x100).toString(16).substring(1))
    }
    return sb.toString()
}
