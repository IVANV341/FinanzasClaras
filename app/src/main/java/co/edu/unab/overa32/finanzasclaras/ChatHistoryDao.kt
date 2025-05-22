// Este archivo define la interfaz `ChatHistoryDao`, que es una Data Access Object (DAO) para la base de datos Room.
// Proporciona métodos para interactuar con el historial de chat de la IA.
// Esto incluye insertar nuevas entradas de chat, obtener todas las entradas del historial,
// obtener una entrada específica por su ID, eliminar una entrada individual y eliminar todo el historial.



package co.edu.unab.overa32.finanzasclaras

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Insert
    suspend fun insertChatHistoryEntry(entry: ChatHistoryEntry): Long

    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC")
    fun getAllChatHistoryEntries(): Flow<List<ChatHistoryEntry>>

    @Query("SELECT * FROM chat_history WHERE id = :entryId LIMIT 1") // ¡ESTE MÉTODO ES CRÍTICO Y DEBE ESTAR!
    suspend fun getChatHistoryEntryById(entryId: Long): ChatHistoryEntry?

    @Query("DELETE FROM chat_history WHERE id = :entryId")
    suspend fun deleteChatHistoryEntry(entryId: Long)

    @Query("DELETE FROM chat_history")
    suspend fun deleteAllChatHistoryEntries()
}