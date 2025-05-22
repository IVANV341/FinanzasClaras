// Este archivo define la clase `GeminiAIRepository`, que se encarga de la comunicación
// con la API de Gemini (en este caso, el modelo "gemma-3-1b-it").
// Su función principal es enviar un 'prompt' a la IA y devolver la respuesta generada.
// Está diseñado para ser 'open' para facilitar las pruebas y el mocking en entornos de vista previa.



package co.edu.unab.overa32.finanzasclaras

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow // Asegúrate de que esta importación esté
import kotlinx.coroutines.flow.MutableStateFlow // Y esta
import kotlinx.coroutines.flow.StateFlow // Y esta

// Este repositorio manejará la comunicación con la API de Gemini
open class GeminiAIRepository { // ¡CAMBIADO a 'open' para permitir la herencia en el Preview!

    private val generativeModel = GenerativeModel(
        modelName = "gemma-3-1b-it",
        apiKey = BuildConfig.GOOGLE_GEMINI_API_KEY
    )

    open suspend fun generateTextResponse(prompt: String): Result<String> { // ¡CAMBIADO a 'open' para permitir el override en el Preview!
        return try {
            val response: GenerateContentResponse = generativeModel.generateContent(prompt)
            val text = response.text ?: "No se pudo generar una respuesta de texto."
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}