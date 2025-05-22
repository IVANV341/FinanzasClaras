// Este archivo define la clase de datos `ChatMessage`, que representa un único mensaje
// dentro de una conversación de chat. Incluye un identificador único, el texto del mensaje,
// un booleano para indicar si el mensaje fue enviado por el usuario o por la IA,
// y un indicador para mostrar si el mensaje está en proceso de carga.
// La anotación `@Serializable` es crucial para permitir que esta clase sea convertida
// a formato JSON para su almacenamiento en la base de datos.


package co.edu.unab.overa32.finanzasclaras

import kotlinx.serialization.Serializable // ¡NUEVO IMPORT!

// Definición del modelo de datos para un mensaje de chat
@Serializable // ¡AÑADIDA! Necesario para la serialización a JSON
data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)