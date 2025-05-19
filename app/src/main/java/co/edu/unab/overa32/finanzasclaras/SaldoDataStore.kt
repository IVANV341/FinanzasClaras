package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Delegado para acceder al DataStore
val Context.dataStore by preferencesDataStore(name = "saldo_prefs")

// Clase SaldoDataStore con miembros 'open' para permitir mocking
open class SaldoDataStore(private val context: Context) {
    companion object {
        val SALDO_KEY = doublePreferencesKey("saldo")
    }

    // Propiedad para obtener el saldo como un Flow. Marcada como 'open'.
    open val getSaldo: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[SALDO_KEY] ?: 0.0
    }

    // Función suspend para guardar el saldo. Marcada como 'open'.
    open suspend fun saveSaldo(saldo: Double) {
        context.dataStore.edit { prefs ->
            prefs[SALDO_KEY] = saldo
        }
    }

    // Si SaldoDataStore tuviera otras funciones o propiedades, irían aquí.
    // Márcalas como 'open' solo si necesitas sobrescribirlas en tests o previews.
}