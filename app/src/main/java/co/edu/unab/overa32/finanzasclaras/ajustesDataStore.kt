// Este archivo Kotlin define la clase AjustesDataStore, que es la responsable de manejar las
// preferencias y configuraciones del usuario de manera persistente en la aplicación.
//
// Utiliza Jetpack DataStore (Preferences DataStore) para almacenar pares clave-valor de forma
// asíncrona y segura, reemplazando el antiguo SharedPreferences.
//
// **Funcionalidades principales que gestiona este DataStore:**
// 1.  **Modo Oscuro (Dark Mode):** Guarda si el usuario ha habilitado o deshabilitado el tema oscuro.
// 2.  **Moneda Seleccionada:** Almacena la divisa preferida del usuario (ej. COP, USD, EUR)
//     para el formateo de montos en toda la aplicación.
// 3.  **Nombre de Usuario:** Guarda un nombre de perfil para el usuario.
//
// Este DataStore se comunica con el DataStore real a través de delegados de Context
// (como 'ajustesDataStoreDelegate' y 'saldoDataStoreDelegate'), los cuales deben estar
// definidos en un archivo singleton como 'DataStoreSingleton.kt'.
//
// Las preferencias se exponen como Kotlin Flows, lo que permite que la interfaz de usuario
// (UI) y otras partes de la aplicación reaccionen automáticamente a los cambios de estas
// configuraciones en tiempo real.


package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

// ELIMINAR: import androidx.datastore.preferences.preferencesDataStore // Esta importación ya no es necesaria aquí

// Claves para las preferencias
object PreferencesKeys {
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
    val USER_NAME = stringPreferencesKey("user_name")
    // ¡IMPORTANTE! Las líneas val Context.saldoDataStoreDelegate y val Context.ajustesDataStoreDelegate
    // NO deben estar aquí. Su definición va en DataStoreSingleton.kt.
}

class AjustesDataStore(private val context: Context) {

    // Modo Oscuro: Lee desde ajustesDataStoreDelegate
    // Este delegado DEBE estar definido en DataStoreSingleton.kt para que se resuelva.
    val isDarkModeEnabled: Flow<Boolean> = context.ajustesDataStoreDelegate.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false
        }

    // Guarda el modo oscuro: Escribe en ajustesDataStoreDelegate
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.ajustesDataStoreDelegate.edit { settings ->
            settings[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    // Moneda Seleccionada: Lee desde ajustesDataStoreDelegate
    val selectedCurrency: Flow<String> = context.ajustesDataStoreDelegate.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_CURRENCY] ?: "COP"
        }

    // Guarda moneda: Escribe en ajustesDataStoreDelegate
    suspend fun setSelectedCurrency(currency: String) {
        context.ajustesDataStoreDelegate.edit { settings ->
            settings[PreferencesKeys.SELECTED_CURRENCY] = currency
        }
    }

    // Nombre de Usuario: Lee desde ajustesDataStoreDelegate
    val userName: Flow<String> = context.ajustesDataStoreDelegate.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: "Usuario"
        }

    // Guarda nombre: Escribe en ajustesDataStoreDelegate
    suspend fun setUserName(name: String) {
        context.ajustesDataStoreDelegate.edit { settings ->
            settings[PreferencesKeys.USER_NAME] = name
        }
    }
}