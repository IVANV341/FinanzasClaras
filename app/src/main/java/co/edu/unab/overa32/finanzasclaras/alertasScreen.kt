package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.* // Usamos Material 3 components
import androidx.compose.runtime.* // Para remember y mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // <-- Importación para @Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // <-- Importación para rememberNavController
import androidx.compose.material3.MaterialTheme // <-- Importación para envolver el preview
import java.text.NumberFormat // Para formatear el saldo como moneda
import java.util.Locale // Para especificar la localización del formato

// --- 1. Clase de Datos para un Umbral de Alerta ---
data class AlertThreshold(
    val id: Long, // Identificador único
    val amount: Double, // Monto del umbral
    val isEnabled: Boolean // Si la alerta está activa
)

// --- 2. Definiciones de Colores (Aproximados a la imagen) ---
val PurpleBackground = Color(0xFF673AB7) // Un púrpura para el fondo
val CardColorDark = Color(0xFF424242) // Un gris oscuro/marrón para las tarjetas de alerta y saldo
val GreenToggleActive = Color(0xFF4CAF50) // Color verde para el interruptor activo
val GrayToggleInactive = Color(0xFFB0BEC5) // Color gris para el interruptor inactivo
val TextColorWhite = Color.White // Texto blanco
val TextColorGray = Color.Gray // Texto gris secundario (si se usa)


// --- 3. Composable de la Pantalla de Alertas ---
@OptIn(ExperimentalMaterial3Api::class) // Para TopAppBar
@Composable
fun AlertasScreen(
    myNavController: NavHostController,
    onBackClick: () -> Unit, // Acción para el botón de volver
    onAddAlertClick: () -> Unit, // Acción para el botón "+" de añadir alerta
    function: () -> Unit // <-- Función adicional que recibe
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas", color = TextColorWhite) }, // Título blanco
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // Usa la lambda onBackClick
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextColorWhite) // Icono blanco
                    }
                },
                actions = {
                    // Botón "+" para añadir nueva alerta
                    IconButton(onClick = onAddAlertClick) { // Usa la lambda onAddAlertClick
                        Icon(Icons.Filled.Add, contentDescription = "Añadir Alerta", tint = TextColorWhite) // Icono blanco
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground) // Fondo púrpura para la barra superior
            )
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .padding(paddingValues) // Aplica el padding del Scaffold
                .fillMaxSize()
                .background(PurpleBackground) // Fondo púrpura para el contenido
                .padding(horizontal = 16.dp, vertical = 24.dp), // Padding alrededor del contenido
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre los elementos principales
        ) {
            // --- Lista de Umbrales de Alerta ---
            // Usamos los datos de ejemplo para el preview:
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre los umbrales individuales
            ) {
                sampleAlertThresholds.forEach { alert ->
                    AlertThresholdItem(alert = alert)
                }
            }

            // --- Espacio para separar umbrales y saldos ---
            Spacer(Modifier.height(32.dp)) // Espacio mayor

            // --- Sección de Saldo Total ---
            SaldoDisplayCard(label = "saldo total:", amount = 15400000.00) // Usa la función auxiliar

            // --- Sección de Saldo Actual ---
            SaldoDisplayCard(label = "saldo actual", amount = 1500000.00) // Usa la misma función

            // Si hubiera espacio restante, puedes empujar los elementos hacia arriba
            // Spacer(Modifier.weight(1f))
        }
    }
}

// --- 4. Composable para un Item Individual de Umbral de Alerta ---
@Composable
fun AlertThresholdItem(alert: AlertThreshold) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Sombra sutil
        colors = CardDefaults.cardColors(containerColor = CardColorDark) // Fondo oscuro
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp) // Padding interno
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Distribuye espacio
        ) {
            // Monto del Umbral (Formateado)
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO")) // Ajusta la localización
            val amountFormatted = format.format(alert.amount)

            Text(
                text = amountFormatted,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColorWhite, // Texto blanco
                modifier = Modifier.weight(1f) // Permite que el texto ocupe espacio
            )

            // Interruptor (Switch)
            // El estado del switch debe ser mutable si quieres que cambie al tocarlo
            // Aquí solo mostramos el estado inicial del dato
            var isChecked by remember { mutableStateOf(alert.isEnabled) } // Estado mutable para el switch

            Switch(
                checked = isChecked, // Estado actual del switch
                onCheckedChange = { enabled ->
                    isChecked = enabled // Actualiza el estado local al interactuar
                    // TODO: Aquí deberías notificar a tu ViewModel o lógica
                    // para actualizar el estado real de la alerta en tu lista/base de datos
                },
                colors = SwitchDefaults.colors( // Colores del switch
                    checkedThumbColor = Color.White, // El "dedo" (círculo) cuando está activo
                    checkedTrackColor = GreenToggleActive, // La "pista" (fondo) cuando está activo
                    uncheckedThumbColor = Color.White, // El "dedo" cuando está inactivo
                    uncheckedTrackColor = GrayToggleInactive // La "pista" cuando está inactivo
                )
            )
        }
    }
}

// --- 5. Composable Auxiliar para Mostrar Saldos ---
@Composable
fun SaldoDisplayCard(label: String, amount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Esquinas redondeadas consistentes
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardColorDark) // Fondo oscuro
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp) // Padding interno
                .fillMaxWidth()
        ) {
            Text(
                text = "$label:", // Etiqueta (saldo total o saldo actual)
                fontSize = 16.sp,
                color = TextColorWhite // Texto blanco
            )
            Spacer(Modifier.height(4.dp)) // Pequeño espacio

            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO")) // Ajusta la localización
            val amountFormatted = format.format(amount)

            Text(
                text = amountFormatted, // Monto formateado
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorWhite // Texto blanco
            )
        }
    }
}

// --- 6. Datos de Ejemplo para los Umbrales de Alerta ---
// En una aplicación real, esto vendría de tu estado o ViewModel
val sampleAlertThresholds = listOf(
    AlertThreshold(1, 680000.00, true),
    AlertThreshold(2, 600000.00, true),
    AlertThreshold(3, 500000.00, true),
    AlertThreshold(4, 200000.00, true) // El último en la imagen también parece activo
)


// --- Función @Preview para AlertasScreen ---
// Esta función va DESPUÉS de la definición de AlertasScreen.
@Preview(showBackground = true, showSystemUi = true, name = "Alertas Screen Preview")
@Composable
fun AlertasScreenPreview() {
    // Aquí proporcionamos el entorno necesario para que el preview funcione.
    MaterialTheme { // Envuelve con el tema Material 3 (o tu tema personalizado)
        // Creamos un NavController de prueba. No navega, solo permite que el código compile.
        val navController = rememberNavController()

        // Proporcionamos lambdas de prueba para las acciones de clic y la función adicional
        val previewOnBackClick: () -> Unit = {
            println("Preview: Botón Volver clickeado") // Puedes poner un log
        }
        val previewOnAddAlertClick: () -> Unit = {
            println("Preview: Botón Añadir Alerta clickeado") // Puedes poner un log
        }
        val previewFunction: () -> Unit = {
            println("Preview: Función adicional llamada") // Puedes poner un log
        }

        // Llama a la Composable que queremos previsualizar
        AlertasScreen(
            myNavController = navController as NavHostController, // Casteo necesario para NavHostController
            onBackClick = previewOnBackClick,
            onAddAlertClick = previewOnAddAlertClick,
            function = previewFunction // Pasa la lambda de prueba para 'function'
        )
    }
}

// --- Cómo integrar en tu MainActivity ---
/*
package co.edu.unab.overa32.finanzasclaras // Tu paquete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Importa tu tema y las pantallas necesarias
import co.edu.unab.overa32.finanzasclaras.ui.theme.FinanzasClarasTheme
// Importa las pantallas que necesites: AlertasScreen, PantallaPrincipalUI, etc.


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
                    composable("alertasScreen") { // Define la ruta para esta pantalla
                         AlertasScreen(
                            myNavController = navController, // Pasas el controlador real
                            onBackClick = { navController.popBackStack() }, // Implementa la navegación real hacia atrás
                            onAddAlertClick = {
                                // TODO: Navegar a la pantalla para añadir/editar alertas
                                // navController.navigate("ruta_añadir_alerta")
                            },
                            function = { /* Implementa la lógica real de esta función si es necesaria */ }
                        )
                    }
                    // Define otras rutas (tablaGastos, addGasto, ajustes, addSaldo, aiScreen)
                }
            }
        }
    }
}
*/