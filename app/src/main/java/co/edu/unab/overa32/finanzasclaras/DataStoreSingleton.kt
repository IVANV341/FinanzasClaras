// Este archivo define los delegados de DataStore como propiedades de extensión de Context.
// Así, puedes acceder a ellos fácilmente desde cualquier Composable o clase que tenga un Context.


package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore // ¡IMPORTANTE! Esta importación debe estar aquí.

// Este archivo define los delegados de DataStore como propiedades de extensión de Context.
// Así, puedes acceder a ellos fácilmente desde cualquier Composable o clase que tenga un Context.
// ¡Estas definiciones DEBEN ESTAR AQUÍ y solo aquí!

val Context.saldoDataStoreDelegate: DataStore<Preferences> by preferencesDataStore(name = "saldo_prefs")
val Context.ajustesDataStoreDelegate: DataStore<Preferences> by preferencesDataStore(name = "ajustes_prefs")