package org.botlaxy.telegramit.core.client.model

data class PhotoRequest(val data: ByteArray, val filename: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoRequest

        if (!data.contentEquals(other.data)) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + filename.hashCode()
        return result
    }
}
