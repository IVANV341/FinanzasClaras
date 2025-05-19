package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.* // Usamos Material 3 components
import androidx.compose.runtime.* // Para remember, mutableStateOf, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview // <-- Importación para @Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // <-- Importación para rememberNavController
import androidx.compose.material3.MaterialTheme // <-- Importación para envolver el preview


// --- 1. Clase Sellada para Representar Tipos de Items de Ajustes ---
// Esto nos ayuda a tener diferentes tipos de filas en nuestra lista
sealed class SettingsItem {
    data class Header(val title: String) : SettingsItem() // Un encabezado de sección
    // Un item clickeable (para navegación o acción), puede tener descripción
    data class ClickableItem(val id: String, val title: String, val description: String? = null, val onClick: () -> Unit) : SettingsItem()
    // Un item con un interruptor (toggle)
    data class ToggleItem(val id: String, val title: String, val description: String? = null, val isEnabled: Boolean, val onToggle: (Boolean) -> Unit) : SettingsItem()
    // Podrías añadir otros tipos si necesitas (ej: TextInputItem)
}

// --- 2. Datos de Ejemplo para la Lista de Ajustes ---
// En una app real, el estado de ToggleItem vendría de tu ViewModel/estado central
val sampleSettingsList: List<SettingsItem> = listOf(
    SettingsItem.Header("General"),
    SettingsItem.ClickableItem("currency", "Cambiar Moneda", "COP - Peso Colombiano", onClick = { /* TODO: Navegar a cambiar moneda */ }),
    SettingsItem.ClickableItem("language", "Idioma", "Español", onClick = { /* TODO: Navegar a cambiar idioma */ }),

    SettingsItem.Header("Notificaciones"),
    // Ejemplo de ToggleItem con estado mutable local solo para el preview
    SettingsItem.ToggleItem("expense_alerts", "Recibir Alertas de Gastos", isEnabled = true, onToggle = { isEnabled ->
        // TODO: Implementar lógica para guardar el estado de la alerta (ej. en SharedPreferences, ViewModel)
        println("Recibir Alertas cambiado a: $isEnabled")
    }),
    SettingsItem.ClickableItem("notification_sound", "Sonido de Notificación", onClick = { /* TODO: Navegar a ajustes de sonido */ }),

    SettingsItem.Header("Apariencia"),
    SettingsItem.ToggleItem("dark_mode", "Modo Oscuro", isEnabled = false, onToggle = { isEnabled ->
        // TODO: Implementar lógica para cambiar el tema de la app
        println("Modo Oscuro cambiado a: $isEnabled")
    }),

    SettingsItem.Header("Cuenta"),
    SettingsItem.ClickableItem("edit_profile", "Editar Perfil", onClick = { /* TODO: Navegar a editar perfil */ }),
    SettingsItem.ClickableItem("logout", "Cerrar Sesión", onClick = { /* TODO: Implementar cerrar sesión */ }), // Podrías darle un estilo diferente

    SettingsItem.Header("Acerca de"),
    // Un item clickeable sin acción, solo para mostrar info (la descripción es el valor)
    SettingsItem.ClickableItem("app_version", "Versión de la Aplicación", "1.0.0", onClick = { /* Opcional: Mostrar diálogo de detalles */ }),
    SettingsItem.ClickableItem("terms", "Términos y Condiciones", onClick = { /* TODO: Abrir URL o navegar a pantalla */ }),
    SettingsItem.ClickableItem("privacy", "Política de Privacidad", onClick = { /* TODO: Abrir URL o navegar a pantalla */ })
)

// --- 3. Composable de la Pantalla de Ajustes ---
@OptIn(ExperimentalMaterial3Api::class) // Para TopAppBar
@Composable
fun AjustesScreen(
    myNavController: NavHostController,
    onBackClick: () -> Unit, // Acción para el botón de volver
    function: () -> Unit // <-- Función adicional que recibe
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", color = MaterialTheme.colorScheme.onSurface) }, // Color de texto desde el tema
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // Usa la lambda onBackClick
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface) // Color de icono desde el tema
                    }
                },
                // Puedes añadir acciones aquí si es necesario (ej: buscar ajustes)
                // actions = { IconButton(...) { Icon(...) } }
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface) // Color de fondo de la barra desde el tema
            )
        }
    ) { paddingValues ->
        // Usamos LazyColumn para la lista de ajustes, eficiente para listas largas
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues) // Aplica el padding del Scaffold
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Color de fondo general desde el tema
        ) {
            // items es un builder que nos permite manejar diferentes tipos de elementos
            items(sampleSettingsList) { item ->
                when (item) {
                    is SettingsItem.Header -> SettingsHeader(title = item.title)
                    is SettingsItem.ClickableItem -> ClickableSettingItem(item = item)
                    is SettingsItem.ToggleItem -> ToggleSettingItem(item = item)
                }
                // Opcional: Añadir un Divider después de cada item (excepto headers)
                // if (item !is SettingsItem.Header) {
                //     Divider(color = Color.LightGray, thickness = 0.5.dp)
                // }
            }
        }
    }
    // Aunque 'function' no se usa visualmente en la UI, se recibe como parámetro.
    // Si se necesitara que hiciera algo en respuesta a un evento de UI, se llamaría aquí.
    // Por ejemplo, si un botón llamara a `function()`.
}

// --- 4. Composable para un Encabezado de Sección ---
@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall, // Estilo de texto para encabezados (ajusta según tu tema)
        color = MaterialTheme.colorScheme.primary, // Color primario para los encabezados
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding
            .padding(top = 16.dp) // Espacio adicional arriba del primer encabezado de una sección
    )
}

// --- 5. Composable para un Item de Ajuste Clickeable ---
@Composable
fun ClickableSettingItem(item: SettingsItem.ClickableItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick) // Hace toda la fila clickeable
            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding interno del item
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Distribuye el espacio
    ) {
        Column(
            modifier = Modifier.weight(1f) // Ocupa la mayor parte del espacio
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge, // Estilo de texto para títulos de items
                color = MaterialTheme.colorScheme.onSurface // Color de texto principal del tema
            )
            item.description?.let { description -> // Si hay descripción, la muestra
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall, // Estilo de texto más pequeño para descripción
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Color de texto secundario del tema
                )
            }
        }

        // Ícono de flecha si es clickeable y lleva a otra pantalla (opcional)
        // Excluimos el item "app_version" y el item "logout" de mostrar la flecha típicamente
        if (item.id != "app_version" && item.id != "logout") {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward, // Ícono de flecha
                contentDescription = null, // No necesita descripción para accesibilidad si es solo visual
                tint = MaterialTheme.colorScheme.onSurfaceVariant // Color del icono
            )
        }
        // Si es el item de Logout, podrías darle un color diferente al texto (ej. rojo)
        // Aquí solo cambiamos el color del texto del título para el logout
        if (item.id == "logout") {
            Text(
                text = item.title, // Repetimos el texto del título
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error // Color de error para indicar acción destructiva
            )
        }
    }
    // Añadimos un Divider después de cada item clickeable
    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

// --- 6. Composable para un Item de Ajuste con Interruptor ---
@Composable
fun ToggleSettingItem(item: SettingsItem.ToggleItem) {
    // Estado local para el switch. En una app real, este estado vendría de un ViewModel
    // o se manejaría directamente en la lambda onToggle para actualizar el estado externo.
    // Para el preview, el estado local 'isChecked' funciona bien.
    var isChecked by remember { mutableStateOf(item.isEnabled) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { // Hace la fila clickeable y alterna el switch
                isChecked = !isChecked // Alterna el estado local
                item.onToggle(isChecked) // Llama a la lambda para notificar al exterior
            })
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 16.dp) // Espacio al final de la columna de texto
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            item.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = isChecked, // Usa el estado local
            onCheckedChange = { enabled ->
                isChecked = enabled // Actualiza el estado local
                item.onToggle(enabled) // Llama a la lambda para notificar al exterior
            },
            // Puedes personalizar los colores del Switch aquí si no te gustan los del tema
            // colors = SwitchDefaults.colors(...) // Descomenta y configura si necesitas colores específicos
        )
    }
    // Añadimos un Divider después de cada item con interruptor
    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}


// --- Función @Preview para AjustesScreen ---
// Esta función va DESPUÉS de la definición de AjustesScreen.
@Preview(showBackground = true, showSystemUi = true, name = "Ajustes Screen Preview")
@Composable
fun AjustesScreenPreview() {
    // Aquí proporcionamos el entorno necesario para que el preview funcione.
    // Es crucial envolverlo en MaterialTheme para que los colores y la tipografía del tema se apliquen.
    MaterialTheme { // Envuelve con el tema Material 3 (o tu tema personalizado)
        // Creamos un NavController de prueba. No navega, solo permite que el código compile.
        val navController = rememberNavController()

        // Proporcionamos lambdas de prueba para las acciones de clic y la función adicional
        val previewOnBackClick: () -> Unit = {
            println("Preview: Botón Volver clickeado") // Puedes poner un log
        }
        val previewFunction: () -> Unit = {
            println("Preview: Función adicional llamada") // Puedes poner un log
        }

        // Llama a la Composable que queremos previsualizar
        AjustesScreen(
            myNavController = navController as NavHostController, // Casteo necesario para NavHostController
            onBackClick = previewOnBackClick,
            function = previewFunction // Pasa la lambda de prueba para 'function'
            // Nota: Los lambdas onClick y onToggle dentro de sampleSettingsList
            // también funcionarán en el preview imprimiendo mensajes.
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
// Importa las pantallas que necesites: AjustesScreen, PantallaPrincipalUI, etc.


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
                    composable("ajustes") { // Define la ruta para esta pantalla
                         AjustesScreen(
                            myNavController = navController, // Pasas el controlador real
                            onBackClick = { navController.popBackStack() }, // Implementa la navegación real hacia atrás
                             function = { /* Implementa la lógica real de esta función si es necesaria */ }
                             // En una app real, los lambdas onClick y onToggle en la lista de ajustes
                             // llamarían a funciones en un ViewModel para actualizar el estado o navegar.
                        )
                    }
                    // Define otras rutas (tablaGastos, addGasto, addSaldo, aiScreen, alertasScreen)
                }
            }
        }
    }
}
*/