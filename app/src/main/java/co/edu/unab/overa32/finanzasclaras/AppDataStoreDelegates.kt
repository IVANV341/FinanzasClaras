// Este archivo centraliza la declaración de los delegados de DataStore para toda la aplicación.
// Define dos delegados principales: `ajustesDataStoreDelegate` para las preferencias generales de ajustes
// (como el modo oscuro) y `saldoDataStoreDelegate` para el almacenamiento del saldo del usuario.
// Esto asegura que cada DataStore se inicialice una única vez y sea accesible de manera consistente
// a través del `Context` de la aplicación.package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Única declaración del delegado para las preferencias de ajustes
// Este es el DataStore que AjustesDataStore usará para guardar el modo oscuro.
val Context.ajustesDataStoreDelegate: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

// Única declaración del delegado para las preferencias de saldo
// Este es el DataStore que SaldoDataStore usará.
val Context.saldoDataStoreDelegate: DataStore<Preferences> by preferencesDataStore(name = "saldo_prefs")