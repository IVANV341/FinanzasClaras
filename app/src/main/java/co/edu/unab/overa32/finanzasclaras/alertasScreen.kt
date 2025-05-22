

// Este archivo define la pantalla `AlertasScreen`, que muestra los umbrales de alerta
// configurados por el usuario. Permite ver el saldo actual y los diferentes tipos de
// alertas (como saldo bajo o alto), con la opción de activar o desactivar cada una.
// También proporciona un botón para añadir nuevas alertas. Interactúa con el
// `AlertsViewModel` para obtener y gestionar los datos de las alertas y el saldo.



package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme
import java.text.NumberFormat
import java.util.Locale

// IMPORTS NECESARIOS
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

// (Mantén tus clases de datos AlertThreshold y definiciones de colores)
// --- 1. Clase de Datos para un Umbral de Alerta ---


// --- 2. Definiciones de Colores (Aproximados a la imagen) ---
val PurpleBackground = Color(0xFF673AB7) // Un púrpura para el fondo
val CardColorDark = Color(0xFF424242) // Un gris oscuro/marrón para las tarjetas de alerta y saldo
val GreenToggleActive = Color(0xFF4CAF50) // Color verde para el interruptor activo
val GrayToggleInactive = Color(0xFFB0BEC5) // Color gris para el interruptor inactivo
val TextColorWhite = Color.White // Texto blanco
val TextColorGray = Color.Gray // Texto gris secundario (si se usa)


// --- 3. Composable de la Pantalla de Alertas ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    myNavController: NavHostController, // Warning: Parameter 'myNavController' is never used - lo dejaremos para mantener la firma
    onBackClick: () -> Unit,
    onAddAlertClick: () -> Unit,
    function: () -> Unit, // Warning: Parameter 'function' is never used - lo dejaremos para mantener la firma
    // INYECTA EL VIEWMODEL CON TODAS LAS DEPENDENCIAS REQUERIDAS POR EL FACTORY
    alertsViewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModelFactory(
            saldoDataStore = SaldoDataStore(LocalContext.current),
            alertThresholdsRepository = AlertThresholdsRepository(AppDatabase.getDatabase(LocalContext.current).alertThresholdDao()),
            applicationContext = LocalContext.current.applicationContext // ¡CORREGIDO Y COMPLETO!
        )
    )
) {
    // Observa el estado de la UI desde el ViewModel
    val uiState by alertsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas", color = TextColorWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextColorWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onAddAlertClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Añadir Alerta", tint = TextColorWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(PurpleBackground)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Mensajes de estado ---
            if (uiState.isLoading) {
                CircularProgressIndicator(color = TextColorWhite)
            } else if (uiState.errorMessage != null) {
                Text("Error: ${uiState.errorMessage}", color = Color.Red)
            }

            // --- Lista de Umbrales de Alerta (Ahora desde el ViewModel) ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.umbralesAlerta.isEmpty() && !uiState.isLoading) {
                    Text("No hay alertas configuradas.", color = TextColorWhite, fontSize = 16.sp)
                } else {
                    uiState.umbralesAlerta.forEach { alert ->
                        AlertThresholdItem(
                            alert = alert,
                            onToggleEnabled = { id, enabled ->
                                alertsViewModel.toggleAlertThreshold(id, enabled) // Conecta el switch al ViewModel
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- Sección de Saldo Total (Ajustado para tomar del ViewModel) ---
            // Asumo que el saldo total es el mismo que el saldo actual para esta pantalla
            SaldoDisplayCard(label = "saldo total:", amount = uiState.saldoActual)

            // --- Sección de Saldo Actual (Ahora desde el ViewModel) ---
            SaldoDisplayCard(label = "saldo actual", amount = uiState.saldoActual)
        }
    }
}

// --- Composable para un Item Individual de Umbral de Alerta ---
@Composable
fun AlertThresholdItem(
    alert: AlertThreshold,
    onToggleEnabled: (Long, Boolean) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardColorDark)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            val amountFormatted = format.format(alert.amount)

            Text(
                text = amountFormatted,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColorWhite,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = alert.isEnabled,
                onCheckedChange = { enabled ->
                    onToggleEnabled(alert.id, enabled)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GreenToggleActive,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GrayToggleInactive
                )
            )
        }
    }
}

// --- Composable Auxiliar para Mostrar Saldos ---
@Composable
fun SaldoDisplayCard(label: String, amount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardColorDark)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "$label:",
                fontSize = 16.sp,
                color = TextColorWhite
            )
            Spacer(Modifier.height(4.dp))

            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            val amountFormatted = format.format(amount)

            Text(
                text = amountFormatted,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorWhite
            )
        }
    }
}

// --- Función @Preview para AlertasScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "Alertas Screen Preview")
@Composable
fun AlertasScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val previewOnBackClick: () -> Unit = { println("Preview: Botón Volver clickeado") }
        val previewOnAddAlertClick: () -> Unit = { println("Preview: Botón Añadir Alerta clickeado") }
        val previewFunction: () -> Unit = { println("Preview: Función adicional llamada") }

        AlertasScreen(
            myNavController = navController as NavHostController,
            onBackClick = previewOnBackClick,
            onAddAlertClick = previewOnAddAlertClick,
            function = previewFunction,
            alertsViewModel = viewModel(
                factory = AlertsViewModelFactory(
                    saldoDataStore = SaldoDataStore(LocalContext.current),
                    alertThresholdsRepository = AlertThresholdsRepository(AppDatabase.getDatabase(LocalContext.current).alertThresholdDao()),
                    applicationContext = LocalContext.current.applicationContext // ¡CORREGIDO Y COMPLETO!
                )
            )
        )
    }
}

// Elimina el sampleAlertThresholds, ya que los datos se manejarán dinámicamente desde la base de datos.
// Si aún lo tienes en tu archivo, bórralo o coméntalo.
// val sampleAlertThresholds = listOf(...)