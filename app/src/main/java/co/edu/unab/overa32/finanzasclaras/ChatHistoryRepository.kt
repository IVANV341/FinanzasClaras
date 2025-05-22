// Este archivo define la clase `ChatHistoryRepository`, la cual sirve como una capa de abstracción
// para interactuar con la base de datos de historial de chat a través de `ChatHistoryDao`.
// Permite guardar nuevas sesiones de chat, recuperar todas las sesiones existentes,
// obtener los detalles y mensajes de una sesión específica, y eliminar sesiones individuales o todo el historial.
// Maneja la serialización y deserialización de los mensajes de chat a/desde formato JSON para su almacenamiento.



package co.edu.unab.overa32.finanzasclaras

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString // Importa encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString // ¡IMPORTANTE! Importa decodeFromString

open class ChatHistoryRepository(private val chatHistoryDao: ChatHistoryDao) {

    suspend fun saveChatSession(messages: List<ChatMessage>, summary: String): Long {
        val messagesJson = Json.encodeToString(messages)
        val entry = ChatHistoryEntry(
            timestamp = System.currentTimeMillis(),
            summary = summary,
            messagesJson = messagesJson
        )
        return chatHistoryDao.insertChatHistoryEntry(entry)
    }

    open fun getAllChatHistorySessions(): Flow<List<ChatHistoryEntry>> {
        return chatHistoryDao.getAllChatHistoryEntries()
    }

    suspend fun getChatSessionById(id: Long): Pair<ChatHistoryEntry, List<ChatMessage>>? {
        val entry = chatHistoryDao.getChatHistoryEntryById(id) // Ahora ChatHistoryDao lo tiene
        return entry?.let {
            // ¡La línea que te daba error ahora debería funcionar!
            val messages = Json.decodeFromString<List<ChatMessage>>(it.messagesJson)
            Pair(it, messages)
        }
    }

    suspend fun deleteChatHistorySession(entryId: Long) {
        chatHistoryDao.deleteChatHistoryEntry(entryId)
    }

    suspend fun deleteAllChatHistorySessions() {
        chatHistoryDao.deleteAllChatHistoryEntries()
    }
}