package org.botlaxy.telegramit.core.conversation.persistence

import org.mapdb.DBMaker
import org.mapdb.Serializer
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class MapDBConversationPersistence(
    private val serializer: PersistenceContextSerializer
) : ConversationPersistence {

    private companion object {
        private const val collection: String = "conversation"
        val dbInstance by lazy {
            val workingDir = Paths.get(System.getProperty("user.dir"))
            val dbFile = workingDir.resolve("conversation.db").toFile()
            DBMaker
                .fileDB(dbFile)
                .fileMmapEnableIfSupported()
                .transactionEnable()
                .make()
        }
        val conversationDB by lazy {
            dbInstance.hashMap(collection, Serializer.STRING, Serializer.BYTE_ARRAY)
                .expireAfterCreate(1440, TimeUnit.MINUTES)
                .createOrOpen()
        }
    }

    override fun saveConversation(id: String, conversationData: ConversationData) {
        val serialized = serializer.serialize(conversationData)
        conversationDB[id] = serialized
        dbInstance.commit()
    }

    override fun getConversation(id: String): ConversationData? {
        conversationDB[id]?.let {
            return serializer.deserialize(it)
        }
        return null
    }

    override fun deleteConversation(id: String) {
        conversationDB.remove(id)
        dbInstance.commit()
    }

    override fun clearConversations() {
        conversationDB.clear()
        dbInstance.commit()
    }

    fun close() {
        dbInstance.close()
    }

}
