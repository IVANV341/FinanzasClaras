/*package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import com.google.ai.client.generativeai.GenerativeModel // <-- Importación clave para usar Gemini
import com.google.ai.client.generativeai.extras.countTokens
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.GenerateContentResponse
// Importaciones para manejar posibles errores de la API (deberían resolverse si la dependencia está bien)
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
//import co.edu.unab.overa32.finanzasclaras.BuildConfig


// Asegúrate de que BuildConfig esté accesible para obtener la API Key
// La línea de importación de BuildConfig DEBE estar descomentada y apuntar al paquete correcto
// COMÉNTALA O ELIMÍNALA TEMPORALMENTE:
// import co.edu.unab.overa32.finanzasclaras.BuildConfig


// Esta clase se encargará de la comunicación con el modelo de IA
class IaRepository { // <-- Nombre de la clase corregido a IaRepository

    // TODO: Reemplaza "gemini-1.5-flash-latest" con el nombre del modelo que quieras usar
    // "gemini-1.5-flash-latest" es rápido y económico.
    // "gemini-1.5-pro-latest" es más potente pero puede tener costos o límites diferentes.
    // Verifica los modelos disponibles en la documentación de Google AI.
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest", // Nombre del modelo a usar
        // *** AQUÍ ES DONDE LO CAMBIAS ***
        // Reemplaza "BuildConfig.GEMINI_API_KEY" por tu clave API real entre comillas dobles.
        apiKey = "TU_CLAVE_API_DE_GEMINI_AQUI" // ¡¡¡ADVERTENCIA: NO HACER ESTO EN PRODUCCIÓN!!!
    )

    // Función suspendida para enviar un mensaje de texto al modelo y obtener una respuesta.
    // Las funciones suspendidas son necesarias para operaciones que toman tiempo (como llamadas de red).
    suspend fun getAIResponse(userMessage: String): String {
        try {
            // Creamos el contenido de la solicitud. El método 'content' de la biblioteca ayuda a construirlo.
            val prompt = content {
                text(userMessage) // Añadimos el texto del mensaje del usuario
            }

            // Enviamos la solicitud al modelo y esperamos la respuesta.
            // generateContent es una función suspend que realiza la llamada de red.
            val response: GenerateContentResponse = generativeModel.generateContent(prompt)

            // Extraemos el texto de la respuesta.
            // La respuesta puede tener múltiples partes y candidatos, simplificamos a la primera parte del primer candidato.
            return response.text ?: "No se recibió respuesta de la IA." // Devuelve el texto o un mensaje por defecto si es nulo

        } catch (e: Exception) {
            // Capturamos cualquier error que ocurra durante la llamada a la API
            // Si las importaciones de excepciones específicas (SerializationException, GoogleGenerativeAIException)
            // te dan error, puedes dejar solo 'catch (e: Exception)'.
            println("Error en la llamada a la API de IA: ${e.message}")
            // Propagamos el error para que la pantalla lo maneje
            throw e
            // Opcional: Devolver un mensaje de error en lugar de relanzar la excepción
            // return "Error al comunicarse con la IA: ${e.localizedMessage}"
        }
    }

    // Opcional: Función para contar tokens si necesitas gestionar límites
    suspend fun countMessageTokens(message: String): Int {
        return generativeModel.countTokens(content { text(message) }).totalTokens
    }

    // TODO: Si necesitas conversación con memoria (historial de mensajes),
    // esta clase debería gestionar un Chat usando model.startChat()
    // y chat.sendMessage() en lugar de model.generateContent().
    // Esto es un paso más avanzado que podemos abordar después si lo necesitas.
    // Ejemplo básico de cómo iniciar un chat (para uso futuro):
    /*
    fun startNewChat() = generativeModel.startChat()
    // Luego usarías chat.sendMessage(content { text(...) })
    */
}

 */