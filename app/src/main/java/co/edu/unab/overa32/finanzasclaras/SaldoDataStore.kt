// app/src/main/java/co/edu/unab/overa32/finanzasclaras/SaldoDataStore.kt
package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
// ELIMINADO: import androidx.datastore.preferences.preferencesDataStore // Ya no es necesario aquí
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ELIMINADO: val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saldo_prefs") // ¡Esta línea debe ELIMINARSE!

open class SaldoDataStore(private val context: Context) {
    companion object {
        val SALDO_KEY = doublePreferencesKey(name = "saldo")
    }

    open val getSaldo: Flow<Double> = context.saldoDataStoreDelegate.data // <-- ¡CAMBIADO AQUÍ!
        .map { prefs ->
            prefs[SALDO_KEY] ?: 0.0
        }

    open suspend fun saveSaldo(saldo: Double) {
        context.saldoDataStoreDelegate.edit { prefs -> // <-- ¡CAMBIADO AQUÍ!
            prefs[SALDO_KEY] = saldo
        }
    }
}