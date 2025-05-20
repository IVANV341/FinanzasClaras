// app/src/main/java/co/edu/unab/overa32/finanzasclaras/AjustesDataStore.kt
package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

// Claves para las preferencias
object PreferencesKeys {
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
    val USER_NAME = stringPreferencesKey("user_name")
}

class AjustesDataStore(private val context: Context) {

    // Modo Oscuro: Lee desde ajustesDataStoreDelegate
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