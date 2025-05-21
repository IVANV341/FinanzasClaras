package co.edu.unab.overa32.finanzasclaras

// Definición del modelo de datos para un mensaje de chat
data class ChatMessage(
    val id: String, // ID único para el mensaje
    val text: String,
    val isUser: Boolean, // True si es mensaje del usuario, False si es de la IA
    val isLoading: Boolean = false // Para mostrar un indicador de carga para respuestas de IA
)