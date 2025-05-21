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
import co.edu.unab.overa32.finanzasclaras.PantallaPrincipalUI
import co.edu.unab.overa32.finanzasclaras.AddGastoCompletoScreen
import co.edu.unab.overa32.finanzasclaras.AddGastoScreen
import co.edu.unab.overa32.finanzasclaras.IaScreen
import co.edu.unab.overa32.finanzasclaras.AjustesScreen
import co.edu.unab.overa32.finanzasclaras.TablaGastosScreen
import co.edu.unab.overa32.finanzasclaras.SaldoScreen

// --- Importaciones de DataStore y Firebase ---
import co.edu.unab.overa32.finanzasclaras.AjustesDataStore
import co.edu.unab.overa32.finanzasclaras.SaldoDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

// --- ¡NUEVAS IMPORTACIONES PARA EL SISTEMA DE ALERTAS Y MONITOR! ---
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.edu.unab.overa32.finanzasclaras.NotificationHelper
import co.edu.unab.overa32.finanzasclaras.AlertasScreen
import co.edu.unab.overa32.finanzasclaras.AddAlertScreen
import co.edu.unab.overa32.finanzasclaras.BalanceMonitor // ¡IMPORTACIÓN NECESARIA!


class MainActivity : ComponentActivity() {

    // ¡NUEVO! Instancia del monitor de saldo
    private lateinit var balanceMonitor: BalanceMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()

        // --- LÓGICA PARA EL SISTEMA DE ALERTAS Y MONITOR ---
        // 1. Crear el canal de notificación (necesario para Android 8.0+)
        NotificationHelper.createNotificationChannel(this)

        // 2. Solicitar permiso de notificación (necesario para Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU es API 33
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // 3. Inicializar y empezar a monitorear el saldo en segundo plano
        // Usamos applicationContext para que el monitor no dependa de la Activity directamente
        val contextForMonitor = applicationContext
        val saldoDataStoreForMonitor = SaldoDataStore(contextForMonitor)
        val alertThresholdsRepositoryForMonitor = AlertThresholdsRepository(AppDatabase.getDatabase(contextForMonitor).alertThresholdDao())

        balanceMonitor = BalanceMonitor(saldoDataStoreForMonitor, alertThresholdsRepositoryForMonitor, contextForMonitor)
        balanceMonitor.startMonitoring() // Inicia el monitoreo en cuanto la app se crea
        // --- FIN LÓGICA SISTEMA DE ALERTAS Y MONITOR ---


        setContent {
            val context = LocalContext.current // Contexto para composables

            // DataStores para la UI principal (pueden ser los mismos que para el monitor)
            val ajustesDataStore = remember { AjustesDataStore(context) }
            val saldoDataStore = remember { SaldoDataStore(context) } // Se sigue usando para la UI

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
                        TablaGastosScreen(
                            myNavController,
                            onNavigateToAlertas = { totalGastos -> myNavController.navigate("alertas") },
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

                    // --- RUTAS COMPOSABLE PARA EL SISTEMA DE ALERTAS (Ya integradas) ---
                    composable("alertas") {
                        AlertasScreen(
                            myNavController = myNavController,
                            onBackClick = { myNavController.popBackStack() },
                            onAddAlertClick = { myNavController.navigate("addAlert") },
                            function = { /* ... */ }
                        )
                    }

                    composable("addAlert") {
                        AddAlertScreen(
                            navController = myNavController
                        )
                    }
                }
            }
        }
    }

    // --- ¡NUEVO! Detener el monitoreo cuando la actividad se destruye ---
    override fun onDestroy() {
        super.onDestroy()
        balanceMonitor.stopMonitoring() // Detiene las corrutinas del monitor
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