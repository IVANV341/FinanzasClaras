package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import android.content.Context // Necesario para SaldoDataStore y File
import android.widget.Toast // Necesario para mostrar mensajes al usuario
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // <-- Importación para rememberNavController
import androidx.compose.material3.MaterialTheme // <-- Importación para envolver el preview
import androidx.compose.ui.tooling.preview.Preview

// Necesarias para Coroutines y DataStore/File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first // <-- Importación para obtener el primer valor del Flow

import java.io.File // <-- Importación para manejo de archivos
import java.text.SimpleDateFormat // <-- Importación para formato de fecha
import java.util.Date // <-- Importación para fecha actual
import java.util.Locale // <-- Importación para localización de fecha

// Asegúrate de que tu clase SaldoDataStore esté importada o en el mismo paquete
// import co.edu.unab.overa32.finanzasclaras.SaldoDataStore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaldoScreen(navController: NavController){
    val context = LocalContext.current
    // Instancia de SaldoDataStore usando el contexto
    // Si da problemas en el preview, considera mockear SaldoDataStore aquí.
    val saldoDataStore = remember { SaldoDataStore(context) }


    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Saldo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Añadir Saldo", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull()
                    if (montoDouble != null && montoDouble > 0) {
                        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        // Si la descripción está vacía, usar un valor por defecto
                        val descMovimiento = if (descripcion.isBlank()) "Ingreso" else descripcion.trim()

                        // --- Lógica para guardar el movimiento y actualizar el saldo ---
                        // Cambiamos el formato y nombre del archivo para registrar el ingreso
                        val lineaMovimiento = "saldo|$descMovimiento|$montoDouble|$currentDate\n" // Añadimos el tipo "saldo"
                        val file = File(context.filesDir, "movimientos.txt") // <-- Nombre del archivo común

                        // Lanzamos una coroutine porque las operaciones de archivo/DataStore pueden ser lentas/suspend
                        CoroutineScope(Dispatchers.IO).launch {
                            // 1. Guardar el movimiento de ingreso en el archivo común
                            file.appendText(lineaMovimiento) // Usa appendText

                            // 2. Leer el saldo actual desde SaldoDataStore
                            val saldoActual = saldoDataStore.getSaldo.first()

                            // 3. Sumarle el monto del ingreso
                            val nuevoSaldoTotal = saldoActual + montoDouble

                            // 4. Guardar el nuevo saldo total en el DataStore
                            saldoDataStore.saveSaldo(nuevoSaldoTotal)

                            // Mostrar mensaje de éxito y volver a la pantalla anterior (en el hilo principal)
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Saldo registrado y actualizado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }

                    } else {
                        Toast.makeText(context, "Por favor ingresa un monto válido mayor a cero", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50), // Color verde
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Saldo")
            }
        }
    }
}

// --- Función @Preview para SaldoScreen ---
// Se mantiene igual. Necesita un mock de SaldoDataStore si no lo recibe como parámetro.
// En este caso, SaldoScreen no recibe SaldoDataStore como parámetro, lo instancia localmente.
// Esto puede ser problemático para mocking en preview. Una mejor práctica sería pasarlo como parámetro.
// Para que el preview compile con la estructura actual, SaldoDataStore debe poder instanciarse con un contexto de preview.
@Preview(showBackground = true, showSystemUi = true, name = "Saldo Screen Preview")
@Composable
fun SaldoScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        // Si SaldoDataStore no requiere parámetros complejos más allá del Context,
        // debería poder instanciarse aquí con LocalContext.current.
        // Si SaldoDataStore tuviera dependencias, necesitarías mockearlo aquí.
        // Como SaldoScreen instancia SaldoDataStore internamente, el preview llamará a esa instanciación.
        // Si SaldoDataStore requiere 'open' miembros para mocks en otras pantallas, manten eso.

        // Llama a la Composable que queremos previsualizar
        SaldoScreen(navController = navController)
    }
}

// --- Tu clase SaldoDataStore ---
/*
// Asegúrate de que esta clase esté en tu proyecto en el paquete correcto y con los miembros 'open'.
// package co.edu.unab.overa32.finanzasclaras
// import android.content.Context
// import androidx.datastore.preferences.core.doublePreferencesKey
// import androidx.datastore.preferences.core.edit
// import androidx.datastore.preferences.preferencesDataStore
// import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.map
// import kotlinx.coroutines.flow.first
// import kotlinx.coroutines.flow.flowOf // Para mocks

// val Context.dataStore by preferencesDataStore(name = "saldo_prefs")

// open class SaldoDataStore(...) {
//    companion object { ... }
//    open val getSaldo: Flow<Double> = ...
//    open suspend fun saveSaldo(saldo: Double) { ... }
// }
*/