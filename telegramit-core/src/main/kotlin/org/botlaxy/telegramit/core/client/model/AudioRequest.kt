package org.botlaxy.telegramit.core.client.model

data class AudioRequest(val audio: ByteArray, val filename: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioRequest

        if (!audio.contentEquals(other.audio)) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = audio.contentHashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}
