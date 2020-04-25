package org.botlaxy.telegramit.core.client.model

data class VideoRequest(
    val video: ByteArray,
    val filename: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VideoRequest

        if (!video.contentEquals(other.video)) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = video.contentHashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}
