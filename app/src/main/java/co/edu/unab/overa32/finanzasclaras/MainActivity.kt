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

// --- Importaciones de tus pantallas (¡Revisar que apunten a los archivos correctos!) ---
import co.edu.unab.overa32.finanzasclaras.PantallaPrincipalUI // Asumimos que está en MainScreen.kt
import co.edu.unab.overa32.finanzasclaras.AddGastoCompletoScreen // Asumimos que está en gastosCompletosScreen.kt (la lógica completa)
import co.edu.unab.overa32.finanzasclaras.AddGastoScreen // Asumimos que está en gastos.kt (el esqueleto/placeholder)
import co.edu.unab.overa32.finanzasclaras.IaScreen
import co.edu.unab.overa32.finanzasclaras.AlertasScreen
import co.edu.unab.overa32.finanzasclaras.AjustesScreen // <-- ¡Importación necesaria!
import co.edu.unab.overa32.finanzasclaras.TablaGastosScreen
import co.edu.unab.overa32.finanzasclaras.SaldoScreen

// --- Importaciones de DataStore y Firebase ---
import co.edu.unab.overa32.finanzasclaras.AjustesDataStore
import co.edu.unab.overa32.finanzasclaras.SaldoDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()

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
                            onAddExpenseClick = { myNavController.navigate("addGastoCompleto") }, // <-- Va a la lógica COMPLETA
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
                        // Esta ruta llama a la pantalla ESQUELETO (AddGastoScreen en gastos.kt)
                        AddGastoScreen(navController = myNavController, saldoDataStore = saldoDataStore, selectedCurrency = selectedCurrency)
                    }

                    composable("addGastoCompleto") { // Esta ruta llama a la lógica COMPLETA (AddGastoCompletoScreen en gastosCompletosScreen.kt)
                        AddGastoCompletoScreen(navController = myNavController, saldoDataStore = saldoDataStore, selectedCurrency = selectedCurrency)
                    }

                    composable("tablaGastos") {
                        TablaGastosScreen(myNavController, onNavigateToAlertas = { totalGastos -> myNavController.navigate("alertas") }, selectedCurrency = selectedCurrency)
                    }

                    composable("addSaldo") {
                        SaldoScreen(myNavController, selectedCurrency = selectedCurrency)
                    }

                    // --- ¡RUTA FALTANTE AÑADIDA! ---
                    composable("ajustes") {
                        AjustesScreen(myNavController, onBackClick = { myNavController.popBackStack() })
                    }
                    // --- FIN DE RUTA AÑADIDA ---

                    // Si CategoriasScreen no existe o no se usa, comenta/elimina esta línea.
                    // composable("categorias") { CategoriasScreen() }
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