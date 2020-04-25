package org.botlaxy.telegramit.core.client.model

data class DocumentRequest(val document: ByteArray, val fileName: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentRequest

        if (!document.contentEquals(other.document)) return false
        if (fileName != other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = document.contentHashCode()
        result = 31 * result + fileName.hashCode()
        return result
    }
}
