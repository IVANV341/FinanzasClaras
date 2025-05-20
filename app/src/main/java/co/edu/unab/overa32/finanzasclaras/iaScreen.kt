package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send // Importa el ícono de enviar
import androidx.compose.material3.* // Usamos Material 3 components
import androidx.compose.runtime.* // Para remember y mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// ELIMINAR: import androidx.compose.ui.graphics.Color // Ya no la necesitamos si no usamos colores fijos
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme // ¡SÍ! Importación para MaterialTheme.colorScheme

// --- 1. Definiciones de Colores --- (¡ESTAS LÍNEAS SE ELIMINAN!)
// val SoftBackgroundColor = Color(0xFFD1C4E9)
// val MessageBubbleColor = Color(0xFF4527A0)
// val InputFieldBackgroundColor = Color.White
// val SendButtonColor = Color(0xFF81C784)
// val TextColorOnDark = Color.White
// val TextColorOnLight = Color.Black
// val PlaceholderTextColor = Color.Gray

// --- 2. Composable de la Pantalla de IA ---
@OptIn(ExperimentalMaterial3Api::class) // Para TopAppBar
@Composable
fun IaScreen(
    myNavController: NavHostController,
    onBackClick: () -> Unit, // Acción para el botón de volver
    function: () -> Unit // Función adicional que recibe
) {
    // Estado para el texto en el campo de entrada
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA", color = MaterialTheme.colorScheme.onPrimary) }, // CAMBIADO: Texto del título sobre el color primario de la barra
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary) // CAMBIADO: Tint del icono sobre el color primario
                    }
                },
                // CAMBIADO: Fondo de la barra superior
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                // CAMBIADO: Fondo principal de la pantalla
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // --- Área para los mensajes (Estáticos por ahora) ---
            MessageBubble(
                text = "tienes dudas?",
                isAiMessage = true,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            MessageBubble(
                text = "• Hola Bienvenido a tu gestor de gastos\n• ¿Que tienes en mente?\n• ¿Necesitas ayuda con algo?",
                isAiMessage = true,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Espacio para que los mensajes empujen el área de entrada hacia abajo ---
            Spacer(Modifier.weight(1f))

            // --- Área de Entrada de Texto y Botón de Enviar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campo de Entrada de Texto
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Ingresa tus dudas o escenario financiero que deseas preguntar") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        // CAMBIADO: Colores del campo de entrada
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        errorContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, // Añadido para el color del texto de entrada
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface // Añadido para el color del texto de entrada
                    )
                )

                // *** BOTÓN DE ENVIAR ***
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            println("Mensaje enviado: $inputText")
                            function()
                            inputText = ""
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    // CAMBIADO: Color del botón de enviar (sugerencia: usa color primario o secundario)
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar Mensaje",
                        tint = MaterialTheme.colorScheme.onSecondary // CAMBIADO: Color del icono para contrastar con secondary
                    )
                }
            }
        }
    }
}

// --- Composable Auxiliar para las "Burbujas" de Mensaje ---
@Composable
fun MessageBubble(text: String, isAiMessage: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // CAMBIADO: Color de la burbuja del mensaje
        colors = CardDefaults.cardColors(
            containerColor = if (isAiMessage) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = text,
            // CAMBIADO: Color del texto dentro de la burbuja
            color = if (isAiMessage) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 16.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}


// --- Función @Preview para IaScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "AI Screen Preview")
@Composable
fun IaScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val previewOnBackClick: () -> Unit = { println("Preview: Botón Volver clickeado") }
        val previewFunction: () -> Unit = { println("Preview: Función adicional llamada") }

        IaScreen(
            myNavController = navController as NavHostController,
            onBackClick = previewOnBackClick,
            function = previewFunction
        )
    }
}