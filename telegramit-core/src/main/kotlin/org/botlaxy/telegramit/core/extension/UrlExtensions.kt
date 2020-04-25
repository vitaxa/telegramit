package org.botlaxy.telegramit.core.extension

import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems

fun URI.getFileSystem(): FileSystem {
    return try {
        FileSystems.getFileSystem(this)
    } catch (e: FileSystemNotFoundException) {
        FileSystems.newFileSystem(this, emptyMap<String, String>())
    }
}
