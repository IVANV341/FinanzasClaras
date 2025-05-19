package co.edu.unab.overa32.finanzasclaras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState // <-- Importación necesaria para collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import co.edu.unab.overa32.finanzasclaras.ui.theme.FinanzasClarasTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- Importa todas tus pantallas que uses en la navegacion ---
import co.edu.unab.overa32.finanzasclaras.PantallaPrincipalUI
import co.edu.unab.overa32.finanzasclaras.IaScreen
// import co.edu.unab.overa32.finanzasclaras.MainScreenWithState // Si usas esta pantalla, impórtala
import co.edu.unab.overa32.finanzasclaras.AlertasScreen
import co.edu.unab.overa32.finanzasclaras.AjustesScreen
import co.edu.unab.overa32.finanzasclaras.AddGastoScreen
import co.edu.unab.overa32.finanzasclaras.TablaGastosScreen
import co.edu.unab.overa32.finanzasclaras.SaldoScreen // Importa la pantalla SaldoScreen

// Asegúrate de que tu clase SaldoDataStore esté en tu proyecto y sea accesible
// import co.edu.unab.overa32.finanzasclaras.SaldoDataStore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el diseño de borde a borde
        setContent {
            FinanzasClarasTheme { // Aplica tu tema Material 3
                val myNavController = rememberNavController()
                val myStartDestination = "main" // La pantalla principal es donde iniciamos
                val context = LocalContext.current // Obtiene el contexto
                // Crea la instancia de SaldoDataStore una vez en el nivel más alto de la UI
                val saldoDataStore = remember { SaldoDataStore(context) }

                // Observa el saldo del DataStore y actualiza el estado Compose.
                // Cualquier cambio en el saldo guardado en DataStore actualizará esta variable.
                val saldoTotalState by saldoDataStore.getSaldo.collectAsState(initial = 0.0)


                NavHost(
                    navController = myNavController,
                    startDestination = myStartDestination, // Inicia en la pantalla "main"
                    modifier = Modifier.fillMaxSize() // El NavHost ocupa todo el espacio disponible
                ) {
                    // --- Definición de Rutas (composables) ---

                    composable("main") {
                        // Pasa el valor actual del saldo observado a PantallaPrincipalUI
                        // saldoTotalState contiene el valor Double, ya no necesitas .value con 'by'
                        PantallaPrincipalUI(saldoTotalState, myNavController)
                    }

                    composable("aiScreen") {
                        // Pasa las lambdas necesarias. onBackClick implementa la navegación hacia atrás.
                        IaScreen(
                            myNavController,
                            onBackClick = { myNavController.popBackStack() }
                        ) { /* Lógica de la función adicional si la necesitas */ }
                    }

                    // Si "gastos" y "tablaGastos" son pantallas diferentes, mantenlas.
                    // Si "gastos" era un nombre antiguo para "tablaGastos", considera eliminar o renombrar una.
                    composable("gastos") {
                        // MainScreenWithState() // Asegúrate de que esta Composable exista y sea correcta
                        Text("Pantalla de Gastos (Placeholder/Antigua?)") // Placeholder si no la usas
                    }

                    composable("alertas") {
                        // Pasa las lambdas necesarias. onAddAlertClick debería navegar a una pantalla para añadir alertas.
                        AlertasScreen(
                            myNavController,
                            onBackClick = { myNavController.popBackStack() }, // Vuelve atrás
                            onAddAlertClick = { /* TODO: Navegar a pantalla para añadir/editar alerta */ }
                        ) { /* Lógica de la función adicional si la necesitas */ }
                    }

                    composable("ajustes") {
                        // Pasa las lambdas necesarias.
                        AjustesScreen(
                            myNavController,
                            onBackClick = { myNavController.popBackStack() }
                        ) { /* Lógica de la función adicional si la necesitas */ }
                    }

                    composable("addGasto") {
                        // Pasa la instancia de SaldoDataStore a AddGastoScreen
                        AddGastoScreen(navController = myNavController, saldoDataStore = saldoDataStore) // <-- ¡Pasamos saldoDataStore!
                    }

                    composable("tablaGastos") {
                        // Pasa las lambdas necesarias. onNavigateToAlertas debería navegar a la pantalla de alertas.
                        // Asegúrate de que TablaGastosScreen pase el totalGastos si AlertasScreen lo espera.
                        TablaGastosScreen(
                            myNavController,
                            onNavigateToAlertas = { totalGastos ->
                                // Aquí puedes navegar a "alertas" y si necesitas pasar el total,
                                // modifica la ruta "alertas" para aceptar argumentos (ej: "alertas/{total}")
                                myNavController.navigate("alertas") // Navega a la pantalla de alertas
                            }
                        )
                    }

                    // --- RUTA PARA AÑADIR SALDO ---
                    composable("addSaldo") { // <--- Define la ruta "addSaldo"
                        SaldoScreen(navController = myNavController) // <--- Llama a la Composable SaldoScreen
                    }

                    // Asegúrate de que todas tus rutas importantes estén definidas aquí.
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