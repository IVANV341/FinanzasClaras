// Este archivo define la entidad de base de datos `ChatHistoryEntry` para Room.
// Esta entidad se utiliza para almacenar un registro completo de una conversación con la IA,
// incluyendo un ID único, una marca de tiempo, un resumen de la conversación y el contenido
// de todos los mensajes serializados como una cadena JSON.
// También incluye `ChatConverters`, que son convertidores de tipo para Room, necesarios para
// serializar y deserializar una lista de `ChatMessage` a/desde una cadena JSON para su almacenamiento.


package co.edu.unab.overa32.finanzasclaras

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString // Importa decodeFromString

// Entidad de Room para guardar el historial de una sesión de chat
@Entity(tableName = "chat_history")
@TypeConverters(ChatConverters::class) // Necesario para guardar List<ChatMessage>
data class ChatHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "timestamp") val timestamp: Long, // Marca de tiempo de la conversación
    @ColumnInfo(name = "summary") val summary: String, // Breve resumen de la conversación
    @ColumnInfo(name = "messages_json") val messagesJson: String // Mensajes serializados a JSON
)

// Convertidores para Room para manejar List<ChatMessage>
class ChatConverters {
    @TypeConverter
    fun fromChatMessageList(messages: List<ChatMessage>): String {
        return Json.encodeToString(messages)
    }

    @TypeConverter
    fun toChatMessageList(messagesJson: String): List<ChatMessage> {
        return Json.decodeFromString(messagesJson)
    }
}
