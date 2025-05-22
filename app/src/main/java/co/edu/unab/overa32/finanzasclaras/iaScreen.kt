// Este archivo define la pantalla `IaScreen`, que es la interfaz principal del asistente de IA.
// Muestra el historial de mensajes entre el usuario y la IA, permite al usuario enviar nuevas preguntas,
// y tiene un botón para ver el historial completo de la conversación en un diálogo.
// El `IaViewModel` gestiona la lógica de la conversación, interactuando con `GeminiAIRepository`
// para las respuestas de la IA y con `SaldoDataStore` y `AlertThresholdsRepository` para
// construir prompts basados en los datos financieros del usuario.





package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History // Import for history icon
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext


// --- 1. Definición del modelo de datos para un mensaje de chat (AHORA EN ARCHIVO SEPARADO: ChatMessage.kt) ---
// La data class ChatMessage debe estar en su propio archivo ChatMessage.kt

// --- 2. ViewModel para IaScreen ---
class IaViewModel(
    private val geminiAIRepository: GeminiAIRepository,
    private val saldoDataStore: SaldoDataStore,
    private val alertThresholdsRepository: AlertThresholdsRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("1", "Hola, bienvenido a tu gestor de gastos.\n¿En qué puedo ayudarte hoy?", isUser = false)
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(System.currentTimeMillis().toString(), text, isUser = true)
        _messages.value = _messages.value + userMessage

        val loadingMessage = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            text = "Escribiendo...",
            isUser = false,
            isLoading = true
        )
        _messages.value = _messages.value + loadingMessage

        viewModelScope.launch {
            try {
                // 1. ¡Construye el prompt con los datos de la app!
                val fullPrompt = buildPromptWithAppData(text)

                // 2. Envía el prompt completo a la API de Gemini
                val responseResult = geminiAIRepository.generateTextResponse(fullPrompt)

                // 3. Remover el mensaje de carga
                _messages.value = _messages.value.filter { !it.isLoading }

                // 4. Añadir la respuesta real de la IA o el error
                responseResult.onSuccess { aiResponse ->
                    val aiMessage = ChatMessage(System.currentTimeMillis().toString(), aiResponse, isUser = false)
                    _messages.value = _messages.value + aiMessage
                }.onFailure { error ->
                    val errorMessage = ChatMessage(System.currentTimeMillis().toString(), "Error: ${error.message ?: "No se pudo conectar con la IA."}", isUser = false)
                    _messages.value = _messages.value + errorMessage
                }
            } catch (e: Exception) {
                // Capturar errores durante la construcción del prompt o la lectura de datos
                _messages.value = _messages.value.filter { !it.isLoading }
                val errorMessage = ChatMessage(System.currentTimeMillis().toString(), "Error al obtener datos: ${e.message ?: "Error desconocido."}", isUser = false)
                _messages.value = _messages.value + errorMessage
            }
        }
    }

    // --- Función para construir el prompt con los datos de la app ---
    private suspend fun buildPromptWithAppData(userQuestion: String): String {
        // Obtener los datos relevantes
        val currentSaldo = saldoDataStore.getSaldo.first() // Leer el saldo
        val movimientos = readMovimientosFromFile() // Leer movimientos del archivo
        val umbralesAlerta = alertThresholdsRepository.getAllAlertThresholds().first() // Leer umbrales

        // Formatear los datos para el prompt
        val formattedSaldo = "El saldo actual del usuario es: $currentSaldo."
        val formattedMovimientos = if (movimientos.isNotEmpty()) {
            "El historial de movimientos del usuario es el siguiente:\n" +
                    movimientos.joinToString("\n") { (tipo, desc, monto, fecha) ->
                        val montoStr = String.format(Locale.getDefault(), "%.2f", monto)
                        "$fecha: ${if (tipo == "saldo") "Ingreso" else "Gasto"} de $montoStr en ${desc.ifBlank { "Sin descripción" }}"
                    }
        } else {
            "El usuario no tiene movimientos registrados."
        }
        val formattedUmbrales = if (umbralesAlerta.isNotEmpty()) {
            "Los umbrales de alerta configurados por el usuario son:\n" +
                    umbralesAlerta.joinToString("\n") { alerta ->
                        "${alerta.type} en ${String.format(Locale.getDefault(), "%.2f", alerta.amount)} (estado: ${if (alerta.isEnabled) "activada" else "desactivada"})"
                    }
        } else {
            "El usuario no tiene umbrales de alerta configurados."
        }

        val formattedTips = """
            Consejos financieros generales que puedes ofrecer:
            - Recomendar establecer presupuestos.
            - Sugerir revisar gastos innecesarios.
            - Aconsejar ahorrar un porcentaje del ingreso.
            - Explicar la importancia de un fondo de emergencia.
            - Orientar sobre cómo usar las alertas para controlar gastos.
            - Promover el registro constante de movimientos para un mejor control.
        """.trimIndent()


        // Construir el prompt completo para Gemma
        val fullPrompt = """
            Eres un asistente financiero amigable y experto llamado Finanzas Claras AI.
            Tu objetivo es ayudar al usuario a entender y gestionar mejor sus finanzas personales basándote en los datos que te proporciono.
            Si la pregunta del usuario se puede responder con los datos que te doy, úsalos.
            Si no hay datos específicos para la pregunta, ofrece consejos generales sobre finanzas o explica que no tienes esa información detallada.
            No inventes datos. Si el usuario te pide un análisis de sus movimientos, puedes mencionar tendencias generales si los datos lo permiten.
            Siempre mantén un tono útil y profesional. Puedes ofrecer consejos financieros generales si la pregunta lo permite.

            Aquí tienes los datos de la cuenta del usuario:
            $formattedSaldo
            $formattedMovimientos
            $formattedUmbrales

            $formattedTips

            ---
            Pregunta del usuario: "$userQuestion"
            ---
            Respuesta:
        """.trimIndent()
        return fullPrompt
    }

    private fun readMovimientosFromFile(): List<MovimientoRecord> {
        val fileName = "movimientos.txt"
        val file = File(applicationContext.filesDir, fileName)
        val movimientosList = mutableListOf<MovimientoRecord>()

        if (file.exists()) {
            try {
                file.readLines().forEach { line ->
                    val parts = line.split("|")
                    if (parts.size == 4) {
                        val tipo = parts[0]
                        val desc = parts[1]
                        val monto = parts[2].toDoubleOrNull() ?: 0.0
                        val fecha = parts[3]
                        movimientosList.add(MovimientoRecord(tipo, desc, monto, fecha))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return movimientosList
    }
}

// --- 3. ViewModelFactory para IaViewModel ---
class IaViewModelFactory(
    private val geminiAIRepository: GeminiAIRepository,
    private val saldoDataStore: SaldoDataStore,
    private val alertThresholdsRepository: AlertThresholdsRepository,
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IaViewModel(geminiAIRepository, saldoDataStore, alertThresholdsRepository, applicationContext) as T
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
    // ¡MODIFICADO! Acepta el contexto de la actividad directamente
    applicationContextFromActivity: Context, // ¡NUEVO PARÁMETRO!
    iaViewModel: IaViewModel = viewModel(
        factory = IaViewModelFactory(
            geminiAIRepository = GeminiAIRepository(),
            saldoDataStore = SaldoDataStore(applicationContextFromActivity), // Usa el contexto pasado
            alertThresholdsRepository = AlertThresholdsRepository(AppDatabase.getDatabase(applicationContextFromActivity).alertThresholdDao()), // Usa el contexto pasado
            applicationContext = applicationContextFromActivity // Usa el contexto pasado
        )
    )
) {
    var inputText by remember { mutableStateOf("") }
    val messages by iaViewModel.messages.collectAsState()
    var showHistoryDialog by remember { mutableStateOf(false) } // State to control dialog visibility


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showHistoryDialog = true }) { // History button
                        Icon(Icons.Default.History, contentDescription = "Ver Historial", tint = MaterialTheme.colorScheme.onPrimary)
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
                items(messages.reversed()) { message ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        MessageBubble(
                            text = message.text,
                            isAiMessage = !message.isUser,
                            isLoading = message.isLoading
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
        // History Dialog
        if (showHistoryDialog) {
            ChatHistoryDialog(
                messages = messages,
                onDismiss = { showHistoryDialog = false }
            )
        }
    }
}

// --- Composable Auxiliar para las "Burbujas" de Mensaje ---
@Composable
fun MessageBubble(text: String, isAiMessage: Boolean, modifier: Modifier = Modifier, isLoading: Boolean = false) {
    Card(
        modifier = modifier,
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

// --- Nuevo Composable para el Dialog de Historial ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryDialog(
    messages: List<ChatMessage>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Historial de Conversación") },
        text = {
            LazyColumn {
                items(messages) { message ->
                    val alignment = if (message.isUser) Alignment.End else Alignment.Start
                    val color = if (message.isUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (message.isUser) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = alignment
                    ) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = color),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = textColor,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}


// --- Función @Preview para IaScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "AI Screen Preview")
@Composable
fun IaScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()

        // Mocks para el Preview
        // Captura el contexto para los mocks
        val context = LocalContext.current
        val mockGeminiAIRepository = remember { // Mueve remember fuera del objeto
            object : GeminiAIRepository() {
                override suspend fun generateTextResponse(prompt: String): Result<String> {
                    return Result.success("Esta es una respuesta de prueba de la IA en el preview para: \"$prompt\"")
                }
            }
        }
        val mockSaldoDataStore = remember { // Mueve remember fuera del objeto
            object : SaldoDataStore(context) { // Pasa la variable de contexto
                override val getSaldo: kotlinx.coroutines.flow.Flow<Double> = kotlinx.coroutines.flow.flowOf(100000.0)
            }
        }
        val mockAlertThresholdsRepository = remember { // Mueve remember fuera del objeto
            object : AlertThresholdsRepository(AppDatabase.getDatabase(context).alertThresholdDao()) { // Pasa la variable de contexto
                override fun getAllAlertThresholds(): kotlinx.coroutines.flow.Flow<List<AlertThreshold>> = kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }

        // Pasamos los mocks al ViewModel en el preview
        IaScreen(
            myNavController = navController,
            onBackClick = { println("Preview: Botón Volver clickeado") },
            applicationContextFromActivity = context.applicationContext, // Contexto para el preview
            iaViewModel = viewModel(
                factory = IaViewModelFactory(
                    geminiAIRepository = mockGeminiAIRepository,
                    saldoDataStore = mockSaldoDataStore,
                    alertThresholdsRepository = mockAlertThresholdsRepository,
                    applicationContext = context.applicationContext // Contexto para el preview
                )
            )
        )
    }
}