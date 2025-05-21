package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// Importaciones para AlertDialog, TextButton, FilledTonalButton, OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import com.google.firebase.auth.FirebaseAuth // ¡NUEVO IMPORT! Para pasar a AuthViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel // ¡NUEVO IMPORT! Para obtener el AuthViewModel


// --- 1. Clase Sellada para Representar Tipos de Items de Ajustes ---
sealed class SettingsItem {
    data class Header(val title: String) : SettingsItem()
    data class ClickableItem(val id: String, val title: String, val description: String? = null, val onClick: (Context) -> Unit) : SettingsItem()
    data class ToggleItem(val id: String, val title: String, val description: String? = null) : SettingsItem()
    data class DialogItem(val id: String, val title: String, val description: String? = null, val onDialogRequested: () -> Unit) : SettingsItem()
}

// --- 2. Composable de la Pantalla de Ajustes ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    myNavController: NavHostController,
    onBackClick: () -> Unit,
    // ¡NUEVO! Inyecta el AuthViewModel para cerrar sesión
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(FirebaseAuth.getInstance()))
) {
    val currentContext = LocalContext.current
    val ajustesDataStore = remember { AjustesDataStore(currentContext) }
    val coroutineScope = rememberCoroutineScope()

    val isDarkModeEnabled by ajustesDataStore.isDarkModeEnabled.collectAsState(initial = false)
    val selectedCurrency by ajustesDataStore.selectedCurrency.collectAsState(initial = "COP")
    val userName by ajustesDataStore.userName.collectAsState(initial = "Usuario")

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var newUserNameInput by remember { mutableStateOf(userName) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            val uri: Uri? = result.data?.getParcelableExtra(Settings.ACTION_SOUND_SETTINGS)
            // Lógica para guardar la URI si es necesario (ej. en AjustesDataStore)
        }
    }

    // --- Lista de Ajustes Dinámica ---
    val settingsList: List<SettingsItem> = remember(
        isDarkModeEnabled, selectedCurrency, userName
    ) {
        listOf(
            SettingsItem.Header("General"),
            SettingsItem.DialogItem(
                id = "currency",
                title = "Cambiar Moneda",
                description = selectedCurrency,
                onDialogRequested = { showCurrencyDialog = true }
            ),
            SettingsItem.ClickableItem(
                id = "language",
                title = "Idioma",
                description = "Español",
                onClick = { context -> /* No hace nada en este ejemplo, podría navegar o abrir un selector */ }
            ),

            SettingsItem.Header("Notificaciones"),
            // ToggleItem para alertas de gastos
            SettingsItem.ToggleItem(
                id = "expense_alerts",
                title = "Recibir Alertas de Gastos",
                description = if (true) "Activado" else "Desactivado" // Puedes vincular esto a DataStore también
            ),
            SettingsItem.ClickableItem(
                id = "notification_sound",
                title = "Sonido de Notificación",
                onClick = { context ->
                    val intent = Intent(Settings.ACTION_SOUND_SETTINGS)
                    ringtonePickerLauncher.launch(intent)
                }
            ),

            SettingsItem.Header("Apariencia"),
            // ToggleItem para Modo Oscuro
            SettingsItem.ToggleItem(
                id = "dark_mode",
                title = "Modo Oscuro",
                description = if (isDarkModeEnabled) "Activado" else "Desactivado"
            ),

            SettingsItem.Header("Cuenta"),
            SettingsItem.DialogItem(
                id = "edit_profile",
                title = "Editar Perfil",
                description = userName,
                onDialogRequested = {
                    newUserNameInput = userName
                    showEditProfileDialog = true
                }
            ),
            // ¡NUEVO ITEM! Botón para Cerrar Sesión
            SettingsItem.ClickableItem(
                id = "logout",
                title = "Cerrar Sesión",
                description = "Cierra tu sesión actual",
                onClick = { context ->
                    authViewModel.signOut() // Llama a la función de cerrar sesión del ViewModel
                    // Después de cerrar sesión, navega de nuevo a la pantalla de login
                    // Asegúrate de limpiar la pila de navegación para que no puedan volver atrás
                    myNavController.navigate("loginScreen") {
                        popUpTo(myNavController.graph.id) { inclusive = true } // Limpia toda la pila de navegación
                    }
                }
            ),

            SettingsItem.Header("Acerca de"),
            SettingsItem.ClickableItem(
                id = "app_version",
                title = "Versión de la Aplicación",
                description = "1.0.0",
                onClick = { context -> /* Opcional: Mostrar un diálogo de información */ }
            ),
            SettingsItem.ClickableItem(
                id = "terms",
                title = "Términos y Condiciones",
                onClick = { context ->
                    val url = "https://www.google.com/policies/terms/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            ),
            SettingsItem.ClickableItem(
                id = "privacy",
                title = "Política de Privacidad",
                onClick = { context ->
                    val url = "https://www.google.com/policies/privacy/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(settingsList) { item ->
                when (item) {
                    is SettingsItem.Header -> SettingsHeader(title = item.title)
                    is SettingsItem.ClickableItem -> ClickableSettingItem(item = item, context = currentContext)
                    is SettingsItem.ToggleItem -> ToggleSettingItem(
                        item = item,
                        context = currentContext,
                        ajustesDataStore = ajustesDataStore,
                        isDarkModeActive = isDarkModeEnabled
                    )
                    is SettingsItem.DialogItem -> DialogSettingItem(item = item)
                }
            }
        }
    }

    // --- Diálogo para Cambiar Moneda ---
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Seleccionar Moneda") },
            text = {
                Column {
                    val currencies = listOf("COP", "USD", "EUR")
                    currencies.forEach { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        ajustesDataStore.setSelectedCurrency(currency)
                                        showCurrencyDialog = false
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (currency == selectedCurrency),
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(currency)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- Diálogo para Editar Perfil ---
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Editar Perfil") },
            text = {
                OutlinedTextField(
                    value = newUserNameInput,
                    onValueChange = { newUserNameInput = it },
                    label = { Text("Tu Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        coroutineScope.launch {
                            ajustesDataStore.setUserName(newUserNameInput)
                            showEditProfileDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// --- 4. Composable para un Encabezado de Sección ---
@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 16.dp)
    )
}

// --- 5. Composable para un Item de Ajuste Clickeable ---
@Composable
fun ClickableSettingItem(item: SettingsItem.ClickableItem, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { item.onClick(context) })
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
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

        if (item.id != "app_version") {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

// --- NUEVO: Composable para un Item que dispara un Diálogo ---
@Composable
fun DialogSettingItem(item: SettingsItem.DialogItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onDialogRequested)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
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
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}


// --- 6. Composable para un Item de Ajuste con Interruptor (MODIFICADO para usar el Context pasado y AjustesDataStore) ---
@Composable
fun ToggleSettingItem(item: SettingsItem.ToggleItem, context: Context, ajustesDataStore: AjustesDataStore, isDarkModeActive: Boolean) {
    val coroutineScope = rememberCoroutineScope()

    // Las SharedPreferences también se recuerdan usando el contexto pasado, NO LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }

    val currentCheckedState = if (item.id == "dark_mode") isDarkModeActive else {
        prefs.getBoolean(item.id, false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                coroutineScope.launch {
                    if (item.id == "dark_mode") {
                        ajustesDataStore.setDarkModeEnabled(!isDarkModeActive)
                    } else {
                        val newChecked = !prefs.getBoolean(item.id, false)
                        prefs.edit().putBoolean(item.id, newChecked).apply()
                        println("Toggle ${item.title} cambiado a: $newChecked (guardado en SharedPreferences)")
                    }
                }
            })
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 16.dp)
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
            checked = currentCheckedState,
            onCheckedChange = { enabled ->
                coroutineScope.launch {
                    if (item.id == "dark_mode") {
                        ajustesDataStore.setDarkModeEnabled(enabled)
                    } else {
                        prefs.edit().putBoolean(item.id, enabled).apply()
                        println("Toggle ${item.title} cambiado a: $enabled (guardado en SharedPreferences)")
                    }
                }
            },
        )
    }
    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}


// --- Función @Preview para AjustesScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "Ajustes Screen Preview")
@Composable
fun AjustesScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        AjustesScreen(
            myNavController = navController,
            onBackClick = { println("Preview: Botón Volver clickeado") },
        )
    }
}