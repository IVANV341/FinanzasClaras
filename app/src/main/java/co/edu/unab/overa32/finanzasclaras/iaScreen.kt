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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview // <-- Importación para @Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // <-- Importación para rememberNavController
import androidx.compose.material3.MaterialTheme // <-- Importación para envolver el preview

// --- 1. Definiciones de Colores --- (Iguales que antes)
val SoftBackgroundColor = Color(0xFFD1C4E9) // Un lavanda/púrpura suave para el fondo
val MessageBubbleColor = Color(0xFF4527A0) // Un púrpura oscuro para los "globos" de mensaje/tarjetas
val InputFieldBackgroundColor = Color.White // Blanco para el fondo del campo de entrada
val SendButtonColor = Color(0xFF81C784) // Un verde para el botón de enviar
val TextColorOnDark = Color.White // Texto blanco sobre fondos oscuros
val TextColorOnLight = Color.Black // Texto negro sobre fondos claros
val PlaceholderTextColor = Color.Gray // Color para el texto de placeholder en el input

// --- 2. Composable de la Pantalla de IA ---
@OptIn(ExperimentalMaterial3Api::class) // Para TopAppBar
@Composable
fun IaScreen(
    myNavController: NavHostController,
    onBackClick: () -> Unit, // Acción para el botón de volver
    function: () -> Unit // <-- Función adicional que recibe
    // Puedes añadir un parámetro para enviar el mensaje si la lógica está fuera
    // onSendMessage: (String) -> Unit
) {
    // Estado para el texto en el campo de entrada
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asistente IA", color = TextColorOnDark) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // Usa la lambda onBackClick
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextColorOnDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MessageBubbleColor)
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(SoftBackgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Ajustamos un poco el padding general
        ) {
            // --- Área para los mensajes (Estáticos por ahora) ---
            MessageBubble(text = "tienes dudas?", isAiMessage = true, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(Modifier.height(8.dp))

            MessageBubble(
                text = "• Hola Bienvenido a tu gestor de gastos\n• ¿Que tienes en mente?\n• ¿Necesitas ayuda con algo?",
                isAiMessage = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Aquí iría la lista dinámica de mensajes, pero por ahora son estáticos

            // --- Espacio para que los mensajes empujen el área de entrada hacia abajo ---
            Spacer(Modifier.weight(1f))

            // --- Área de Entrada de Texto y Botón de Enviar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Padding vertical para la fila de entrada
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campo de Entrada de Texto
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Ingresa tus dudas o escenario financiero que deseas preguntar") }, // Placeholder text más largo
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputFieldBackgroundColor,
                        unfocusedContainerColor = InputFieldBackgroundColor,
                        disabledContainerColor = InputFieldBackgroundColor,
                        errorContainerColor = InputFieldBackgroundColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = TextColorOnLight,
                        focusedLabelColor = PlaceholderTextColor,
                        unfocusedLabelColor = PlaceholderTextColor
                    )
                )

                // *** BOTÓN DE ENVIAR ***
                Button(
                    onClick = {
                        // TODO: Implementar acción de enviar el mensaje
                        if (inputText.isNotBlank()) {
                            println("Mensaje enviado: $inputText") // Ejemplo: Imprime en la consola
                            // onSendMessage(inputText) // Llama a la lambda si la agregaste
                            function() // Llama a la función adicional
                            inputText = "" // Limpiar campo
                        }
                    },
                    modifier = Modifier
                        .size(56.dp), // Tamaño del área del botón
                    shape = RoundedCornerShape(28.dp), // Forma redondeada consistente
                    colors = ButtonDefaults.buttonColors(containerColor = SendButtonColor), // Color verde
                    contentPadding = PaddingValues(0.dp) // Elimina padding interno
                ) {
                    Icon( // Usa el composable Icon
                        imageVector = Icons.Default.Send, // El ícono de enviar
                        contentDescription = "Enviar Mensaje",
                        tint = TextColorOnDark // Color del ícono (blanco)
                    )
                }
            }
        }
    }
}

// --- Composable Auxiliar para las "Burbujas" de Mensaje (Igual que antes) ---
@Composable
fun MessageBubble(text: String, isAiMessage: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MessageBubbleColor) // Usa el color definido
    ) {
        Text(
            text = text,
            color = TextColorOnDark, // Usa el color definido
            fontSize = 16.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}


// --- Función @Preview para IaScreen ---
// Esta función va DESPUÉS de la definición de IaScreen.
@Preview(showBackground = true, showSystemUi = true, name = "AI Screen Preview")
@Composable
fun IaScreenPreview() {
    // Aquí proporcionamos el entorno necesario para que el preview funcione.
    MaterialTheme { // Envuelve con el tema Material 3 (o tu tema personalizado si tienes uno)
        // Creamos un NavController de prueba. No navega, solo permite que el código compile.
        val navController = rememberNavController()

        // Proporcionamos lambdas de prueba para onBackClick y function
        val previewOnBackClick: () -> Unit = {
            println("Preview: Botón Volver clickeado") // Puedes poner un log
        }
        val previewFunction: () -> Unit = {
            println("Preview: Función adicional llamada") // Puedes poner un log
        }


        // Llama a la Composable que queremos previsualizar
        IaScreen(
            myNavController = navController as NavHostController, // Casteo necesario para NavHostController
            onBackClick = previewOnBackClick,
            function = previewFunction
        )
    }
}

// --- Cómo integrar en tu MainActivity --- (Igual que antes)
/*
package co.edu.unab.overa32.finanzasclaras // Tu paquete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Importa tu tema y la pantalla de IA, etc.
import co.edu.unab.overa32.finanzasclaras.ui.theme.FinanzasClarasTheme
// Importa las pantallas que necesites: IaScreen, PantallaPrincipalUI, etc.


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanzasClarasTheme { // Aplica tu tema
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "mainScreen") {
                    composable("mainScreen") {
                        // Aquí llamas a PantallaPrincipalUI con los parámetros correctos
                        // PantallaPrincipalUI(saldoTotal = ..., navController = navController)
                    }
                    composable("aiScreen") {
                         IaScreen(
                            myNavController = navController, // Pasas el controlador real
                            onBackClick = { navController.popBackStack() }, // Implementa la navegación real hacia atrás
                            function = { /* Implementa la lógica real de esta función */ }
                            // onSendMessage = { message -> ... } // Implementa la lógica de enviar
                        )
                    }
                    // Define otras rutas (tablaGastos, addGasto, ajustes, addSaldo)
                }
            }
        }
    }
}
*/