package org.botlaxy.telegramit.core.client.model

data class VoiceRequest(val voice: ByteArray, val filename: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VoiceRequest

        if (!voice.contentEquals(other.voice)) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = voice.contentHashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}
