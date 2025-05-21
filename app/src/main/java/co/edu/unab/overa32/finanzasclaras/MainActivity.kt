package co.edu.unab.overa32.finanzasclaras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text // Mantener si se usa Text directamente en algún composable aquí
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview // Mantener si se usa Preview directamente aquí
import co.edu.unab.overa32.finanzasclaras.ui.theme.FinanzasClarasTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.app.Activity // Necesario para (context as? Activity)?.finishAffinity()

// --- Importaciones de tus pantallas existentes (¡Revisar que apunten a los archivos correctos!) ---
import co.edu.unab.overa32.finanzasclaras.PantallaPrincipalUI // Asumimos que está en MainScreen.kt
import co.edu.unab.overa32.finanzasclaras.AddGastoCompletoScreen // Asumimos que está en gastosCompletosScreen.kt (la lógica completa)
import co.edu.unab.overa32.finanzasclaras.AddGastoScreen // Asumimos que está en gastos.kt (el esqueleto/placeholder)
import co.edu.unab.overa32.finanzasclaras.IaScreen
// import co.edu.unab.overa32.finanzasclaras.AlertasScreen // <--- Esta la usaremos de tu proyecto de alertas
import co.edu.unab.overa32.finanzasclaras.AjustesScreen
import co.edu.unab.overa32.finanzasclaras.TablaGastosScreen
import co.edu.unab.overa32.finanzasclaras.SaldoScreen

// --- Importaciones de DataStore y Firebase ---
import co.edu.unab.overa32.finanzasclaras.AjustesDataStore
import co.edu.unab.overa32.finanzasclaras.SaldoDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

// --- ¡NUEVAS IMPORTACIONES PARA EL SISTEMA DE ALERTAS! ---
import android.Manifest // Para permisos de notificación
import android.content.pm.PackageManager // Para permisos de notificación
import android.os.Build // Para permisos de notificación
import androidx.core.app.ActivityCompat // Para permisos de notificación
import androidx.core.content.ContextCompat // Para permisos de notificación
import co.edu.unab.overa32.finanzasclaras.NotificationHelper // El helper para notificaciones
import co.edu.unab.overa32.finanzasclaras.AlertasScreen // La pantalla de alertas
import co.edu.unab.overa32.finanzasclaras.AddAlertScreen // La pantalla para añadir alertas


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()

        // --- ¡NUEVA LÓGICA PARA EL SISTEMA DE ALERTAS! ---
        // 1. Crear el canal de notificación (necesario para Android 8.0+)
        NotificationHelper.createNotificationChannel(this)

        // 2. Solicitar permiso de notificación (necesario para Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU es API 33
            // Verifica si el permiso ya está concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Solicita el permiso al usuario
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101) // 101 es un request code arbitrario
            }
        }
        // --- FIN LÓGICA SISTEMA DE ALERTAS ---


        setContent {
            val context = LocalContext.current

            val ajustesDataStore = remember { AjustesDataStore(context) }
            val saldoDataStore = remember { SaldoDataStore(context) }

            val isDarkModeEnabled by ajustesDataStore.isDarkModeEnabled.collectAsState(initial = false)
            val selectedCurrency by ajustesDataStore.selectedCurrency.collectAsState(initial = "COP")

            FinanzasClarasTheme(darkTheme = isDarkModeEnabled) {
                val myNavController = rememberNavController()
                val myStartDestination = "main"

                val saldoTotalState by saldoDataStore.getSaldo.collectAsState(initial = 0.0)

                NavHost(
                    navController = myNavController,
                    startDestination = myStartDestination,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("main") {
                        PantallaPrincipalUI(
                            saldoTotal = saldoTotalState,
                            onAddExpenseClick = { myNavController.navigate("addGastoCompleto") },
                            onTablaGastosClick = { myNavController.navigate("tablaGastos") },
                            onConfiguracionClick = { myNavController.navigate("ajustes") },
                            onPreguntasClick = { myNavController.navigate("aiScreen") },
                            onAddSaldoClick = { myNavController.navigate("addSaldo") },
                            onSalirClick = { (context as? Activity)?.finishAffinity() },
                            selectedCurrency = selectedCurrency
                        )
                    }

                    composable("aiScreen") {
                        IaScreen(myNavController, onBackClick = { myNavController.popBackStack() }) { /* Lógica adicional */ }
                    }

                    composable("gastos") {
                        AddGastoScreen(navController = myNavController, saldoDataStore = saldoDataStore, selectedCurrency = selectedCurrency)
                    }

                    composable("addGastoCompleto") {
                        AddGastoCompletoScreen(navController = myNavController, saldoDataStore = saldoDataStore, selectedCurrency = selectedCurrency)
                    }

                    composable("tablaGastos") {
                        // Esta es la pantalla que ya tiene un botón/acción para ir a "alertas"
                        TablaGastosScreen(
                            myNavController,
                            onNavigateToAlertas = { totalGastos -> myNavController.navigate("alertas") }, // La ruta "alertas" se define abajo
                            selectedCurrency = selectedCurrency
                        )
                    }

                    composable("addSaldo") {
                        SaldoScreen(myNavController, selectedCurrency = selectedCurrency)
                    }

                    composable("ajustes") {
                        AjustesScreen(myNavController, onBackClick = { myNavController.popBackStack() })
                    }

                    // Si CategoriasScreen no existe o no se usa, comenta/elimina esta línea.
                    // composable("categorias") { CategoriasScreen() }

                    // --- ¡NUEVAS RUTAS COMPOSABLE PARA EL SISTEMA DE ALERTAS! ---
                    composable("alertas") { // Esta es la ruta a la que navega TablaGastosScreen
                        AlertasScreen(
                            myNavController = myNavController,
                            onBackClick = { myNavController.popBackStack() },
                            onAddAlertClick = { myNavController.navigate("addAlert") }, // Ruta para añadir una nueva alerta
                            function = { /* Parámetro sin usar en AlertasScreen, pero se mantiene la firma */ }
                        )
                    }

                    composable("addAlert") { // Ruta para añadir una nueva alerta
                        AddAlertScreen(
                            navController = myNavController
                            // El ViewModel para AddAlertScreen se inyecta directamente allí con LocalContext.current
                        )
                    }
                    // --- FIN NUEVAS RUTAS SISTEMA DE ALERTAS ---
                }
            }
        }
    }
}

// Las funciones Greeting y GreetingPreview se mantienen si las necesitas,
// pero no forman parte de la navegación principal definida arriba.

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FinanzasClarasTheme {
        Greeting("Android")
    }
}