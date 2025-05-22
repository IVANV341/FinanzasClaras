// Este archivo es la actividad principal de la aplicación, `MainActivity`.
// Se encarga de la configuración inicial de la aplicación, como la habilitación
// del modo EdgeToEdge, la inicialización de Firebase, la creación del canal de notificaciones
// y la solicitud de permisos. Es el punto de entrada para la composición de la UI
// de Jetpack Compose, gestionando la navegación entre las diferentes pantallas
// de la aplicación (Login, Registro, Principal, Gastos, Alertas, Ajustes, etc.)
// utilizando `NavController`. También inicializa y gestiona el `BalanceMonitor`
// para las alertas en segundo plano.




package co.edu.unab.overa32.finanzasclaras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import co.edu.unab.overa32.finanzasclaras.ui.theme.FinanzasClarasTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.app.Activity

// --- Importaciones de tus pantallas existentes ---
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
import co.edu.unab.overa32.finanzasclaras.BalanceMonitor

// --- Importaciones para la Splash Screen y el Sistema de Autenticación ---
import co.edu.unab.overa32.finanzasclaras.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect


class MainActivity : ComponentActivity() {

    // Instancia del monitor de saldo
    private lateinit var balanceMonitor: BalanceMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa FirebaseApp si no lo está ya. Esto es necesario para Firebase Auth y Firestore.
        // FirebaseApp.initializeApp(this) se llama en la versión que me enviaste.
        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance() // Si usas Firestore en otras partes


        // --- LÓGICA PARA EL SISTEMA DE ALERTAS Y MONITOR ---
        // 1. Crear el canal de notificación (necesario para Android 8.0+)
        NotificationHelper.createNotificationChannel(this)

        // 2. Solicitar permiso de notificación (necesario para Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU es API 33
            // Verifica si el permiso ya está concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Solicita el permiso al usuario
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // 3. Inicializar y empezar a monitorear el saldo en segundo plano
        val contextForMonitor = applicationContext
        val saldoDataStoreForMonitor = SaldoDataStore(contextForMonitor)
        val alertThresholdsRepositoryForMonitor = AlertThresholdsRepository(AppDatabase.getDatabase(contextForMonitor).alertThresholdDao())

        balanceMonitor = BalanceMonitor(saldoDataStoreForMonitor, alertThresholdsRepositoryForMonitor, contextForMonitor)
        balanceMonitor.startMonitoring() // Inicia el monitoreo en cuanto la app se crea
        // --- FIN LÓGICA SISTEMA DE ALERTAS Y MONITOR ---


        setContent {
            val context = LocalContext.current // Contexto para composables

            val ajustesDataStore = remember { AjustesDataStore(context) }
            val saldoDataStore = remember { SaldoDataStore(context) }

            val isDarkModeEnabled by ajustesDataStore.isDarkModeEnabled.collectAsState(initial = false)
            val selectedCurrency by ajustesDataStore.selectedCurrency.collectAsState(initial = "COP")

            // ¡NUEVO! Instancia del AuthViewModel
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(FirebaseAuth.getInstance()))
            // Observa el estado de autenticación
            val authState by authViewModel.authState.collectAsState()


            FinanzasClarasTheme(darkTheme = isDarkModeEnabled) {
                val myNavController = rememberNavController()

                // Decide la pantalla de inicio después del Splash Screen
                // Esta lógica se ejecuta una vez cuando el composable entra en composición.
                // Redirige al usuario a 'main' si está autenticado, o a 'loginScreen' si no lo está.
                LaunchedEffect(authState.isAuthenticated) {
                    if (!authState.isLoading) { // Asegurarse de que el estado inicial no cause navegación
                        if (authState.isAuthenticated) {
                            myNavController.navigate("main") {
                                // Limpia la pila para que no pueda volver a Splash/Login
                                popUpTo("splashScreen") { inclusive = true }
                            }
                        } else {
                            myNavController.navigate("loginScreen") {
                                // Limpia la pila para que no pueda volver a Splash
                                popUpTo("splashScreen") { inclusive = true }
                            }
                        }
                    }
                }

                // La startDestination inicial del NavHost siempre será la SplashScreen.
                // Luego el LaunchedEffect se encargará de la redirección.
                val myStartDestination = "splashScreen" // La aplicación siempre inicia con la Splash Screen

                val saldoTotalState by saldoDataStore.getSaldo.collectAsState(initial = 0.0)

                NavHost(
                    navController = myNavController,
                    startDestination = myStartDestination,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // --- RUTA PARA LA SPLASH SCREEN ---
                    composable("splashScreen") {
                        SplashScreen(navController = myNavController)
                    }

                    // --- RUTAS PARA AUTENTICACIÓN ---
                    composable("loginScreen") {
                        LoginScreen(navController = myNavController, authViewModel = authViewModel)
                    }
                    composable("registerScreen") {
                        RegisterScreen(navController = myNavController, authViewModel = authViewModel)
                    }

                    // --- RUTA PARA LA PANTALLA PRINCIPAL (MAIN) ---
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

                    // --- RUTAS PARA LAS PANTALLAS DE TU APP ---
                    composable("aiScreen") {
                        IaScreen(
                            myNavController = myNavController,
                            onBackClick = { myNavController.popBackStack() },
                            applicationContextFromActivity = applicationContext // ¡AÑADIDO!
                        )
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

                    // --- RUTAS COMPOSABLE PARA EL SISTEMA DE ALERTAS ---
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

    // Detener el monitoreo cuando la actividad se destruye para liberar recursos
    override fun onDestroy() {
        super.onDestroy()
        balanceMonitor.stopMonitoring()
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