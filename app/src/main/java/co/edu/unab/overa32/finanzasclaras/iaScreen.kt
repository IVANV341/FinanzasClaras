package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // ¡AÑADIDA! Importación para asStateFlow
import androidx.compose.runtime.rememberCoroutineScope


import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse


// --- 1. Definición del modelo de datos para un mensaje de chat ---


// --- 2. ViewModel para IaScreen ---
class IaViewModel(private val geminiAIRepository: GeminiAIRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("1", "Hola, bienvenido a tu gestor de gastos.\n¿En qué puedo ayudarte hoy?", isUser = false)
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow() // <-- asStateFlow ahora reconocido

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // 1. Añadir el mensaje del usuario
        val userMessage = ChatMessage(System.currentTimeMillis().toString(), text, isUser = true)
        _messages.value = _messages.value + userMessage

        // 2. Añadir un mensaje de carga de la IA
        val loadingMessage = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            text = "Escribiendo...",
            isUser = false,
            isLoading = true
        )
        _messages.value = _messages.value + loadingMessage

        // 3. Llamar a la API de Gemini
        viewModelScope.launch {
            val responseResult = geminiAIRepository.generateTextResponse(text)

            // 4. Remover el mensaje de carga
            _messages.value = _messages.value.filter { !it.isLoading }

            // 5. Añadir la respuesta real de la IA o el error
            responseResult.onSuccess { aiResponse ->
                val aiMessage = ChatMessage(System.currentTimeMillis().toString(), aiResponse, isUser = false)
                _messages.value = _messages.value + aiMessage
            }.onFailure { error ->
                val errorMessage = ChatMessage(System.currentTimeMillis().toString(), "Error: ${error.message ?: "No se pudo conectar con la IA."}", isUser = false)
                _messages.value = _messages.value + errorMessage
            }
        }
    }
}

// --- 3. ViewModelFactory para IaViewModel ---
class IaViewModelFactory(private val geminiAIRepository: GeminiAIRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IaViewModel(geminiAIRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


// --- 4. Composable de la Pantalla de IA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IaScreen(
    myNavController: NavHostController,
    onBackClick: () -> Unit,
    iaViewModel: IaViewModel = viewModel(factory = IaViewModelFactory(GeminiAIRepository()))
) {
    var inputText by remember { mutableStateOf("") }
    val messages by iaViewModel.messages.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // --- Área para los mensajes (dinámicos y desplazables) ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true // Para que los mensajes nuevos aparezcan abajo
            ) {
                // Invertimos la lista para que el mensaje más reciente esté al final
                items(messages.reversed()) { message ->
                    // ¡CORREGIDO! Usamos un Row para alinear la burbuja dentro de LazyColumn
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start // Alinea mensajes
                    ) {
                        MessageBubble(
                            text = message.text,
                            isAiMessage = !message.isUser,
                            isLoading = message.isLoading
                            // No se usa modifier.fillMaxWidth() en la burbuja para que se adapte al contenido
                        )
                    }
                }
            }

            // --- Área de Entrada de Texto y Botón de Enviar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Escribe tu pregunta...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        errorContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            iaViewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar Mensaje",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

// --- Composable Auxiliar para las "Burbujas" de Mensaje ---
@Composable
fun MessageBubble(text: String, isAiMessage: Boolean, modifier: Modifier = Modifier, isLoading: Boolean = false) {
    Card(
        modifier = modifier, // Ya no aplicamos align aquí
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAiMessage) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = text,
                color = if (isAiMessage) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 16.sp
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp).padding(top = 4.dp),
                    color = if (isAiMessage) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSecondaryContainer,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}


// --- Función @Preview para IaScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "AI Screen Preview")
@Composable
fun IaScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val mockGeminiAIRepository = remember {
            object : GeminiAIRepository() {
                // Sobrescribe generateTextResponse para el preview
                override suspend fun generateTextResponse(prompt: String): Result<String> {
                    return Result.success("Esta es una respuesta de prueba de la IA en el preview para: \"$prompt\"")
                }
            }
        }
        // Pasamos el mock al ViewModel en el preview
        IaScreen(
            myNavController = navController,
            onBackClick = { println("Preview: Botón Volver clickeado") },
            iaViewModel = viewModel(factory = IaViewModelFactory(mockGeminiAIRepository))
        )
    }
}