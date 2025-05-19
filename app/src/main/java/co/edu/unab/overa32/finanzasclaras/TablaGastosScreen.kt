package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // <-- Importación para @Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // <-- Importación para rememberNavController
import androidx.compose.material3.MaterialTheme // <-- Importación para envolver el preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat // Necesaria para formatear el monto
import java.util.Locale // Necesaria para especificar la localización del formato
import java.text.SimpleDateFormat // Necesaria para parsear/formatear fechas si se ordena


// --- Nueva Clase de Datos para Movimiento (Gasto o Saldo) ---
data class Movimiento(
    val tipo: String, // "gasto" o "saldo"
    val descripcion: String,
    val monto: Double,
    val fecha: String // En formato "dd/MM/yyyy"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablaGastosScreen (navController: NavController, onNavigateToAlertas: (Double) -> Unit){
    val context = LocalContext.current
    // Aunque SaldoDataStore no se usa para mostrar la lista, lo instanciamos si la función borrarArchivoGastos lo usa,
    // pero la lógica de borrado debe ser revisada para no borrar el SaldoDataStore accidentalmente.
    // Si borrarArchivoGastos solo borra el archivo de movimientos, no necesita SaldoDataStore.
    // val saldoDataStore = remember { SaldoDataStore(context) } // Eliminar si no se usa

    // Cambiamos el estado a una lista de Movimiento
    var movimientos by remember { mutableStateOf(listOf<Movimiento>()) }

    // Función para borrar el archivo de movimientos
    fun borrarArchivoMovimientos() {
        val file = File(context.filesDir, "movimientos.txt") // <-- Nombre del archivo cambiado
        if (file.exists()) {
            file.delete()
            movimientos = emptyList() // Limpiar la lista en memoria
            Toast.makeText(context, "Historial de movimientos borrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No existe historial de movimientos", Toast.LENGTH_SHORT).show()
        }
        // Opcional: Si borrar todo implica resetear el saldo, también deberías resetear SaldoDataStore aquí.
        // CoroutineScope(Dispatchers.IO).launch { saldoDataStore.saveSaldo(0.0) }
    }

    // LaunchedEffect para cargar datos desde el archivo movimientos.txt
    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "movimientos.txt") // <-- Lee del nuevo archivo
        if (file.exists()) {
            val lines = file.readLines()
                .filter { it.isNotBlank() }
                .map { it.trim() }
            movimientos = lines.mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size == 4) { // Esperamos 4 partes: tipo|desc|monto|fecha
                    val tipo = parts[0].trim()
                    val desc = parts[1].trim()
                    val monto = parts[2].trim().toDoubleOrNull()
                    val fecha = parts[3].trim()
                    if (monto != null && (tipo == "gasto" || tipo == "saldo")) { // Validar tipo
                        Movimiento(tipo, desc, monto, fecha)
                    } else null // Ignorar líneas con formato incorrecto o tipo desconocido
                } else null // Ignorar líneas con número de partes incorrecto
            }
            // Opcional: Ordenar movimientos por fecha (requiere parsear fechas)
            // val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // movimientos = movimientos.sortedBy { dateFormat.parse(it.fecha) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Movimientos") }, // <-- Título de la pantalla cambiado
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFE57373)) // Fondo rojo suave
                .padding(16.dp)
        ) {
            // Encabezados de la tabla
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tipo", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.5f)) // Nueva columna Tipo
                Text("Descripción", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f).padding(start = 4.dp))
                Text("Monto", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.7f).padding(start = 4.dp))
                Text("Fecha", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.7f).padding(start = 4.dp))
                Spacer(modifier = Modifier.width(24.dp)) // Espacio para el icono de borrar
            }
            Divider(thickness = 1.dp, color = Color.White)
            Spacer(Modifier.height(8.dp))

            // Formato de moneda para mostrar el monto
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

            // Lista de Movimientos
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(movimientos) { index, movimiento ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Muestra el tipo (Gasto/Saldo)
                        Text(
                            movimiento.tipo.capitalize(Locale.getDefault()), // Capitalizar la primera letra
                            modifier = Modifier.weight(0.5f).padding(end = 4.dp),
                            color = Color.White // O podrías colorear según el tipo
                        )
                        Text(movimiento.descripcion, modifier = Modifier.weight(1f).padding(end = 4.dp), color = Color.White)

                        // Muestra el monto, quizás con color diferente según el tipo
                        val montoColor = when (movimiento.tipo) {
                            "gasto" -> Color.Red // Rojo para gastos
                            "saldo" -> Color.Green // Verde para saldos
                            else -> Color.White // Blanco por defecto
                        }
                        Text(
                            format.format(movimiento.monto), // Formatear el monto
                            modifier = Modifier.weight(0.7f).padding(end = 4.dp),
                            color = montoColor, // Aplica el color según el tipo
                            fontWeight = FontWeight.Bold // Opcional: negrita para montos
                        )

                        Text(movimiento.fecha, modifier = Modifier.weight(0.7f).padding(end = 4.dp), color = Color.White)

                        // Botón de eliminar (elimina de la lista y reescribe el archivo)
                        // TODO: La eliminación de un movimiento debería también actualizar el SaldoDataStore
                        // (Sumar el monto si es un gasto eliminado, restar si es un saldo eliminado).
                        IconButton(onClick = {
                            // Lógica para eliminar el movimiento de la lista y el archivo
                            val movimientoEliminado = movimientos[index] // Guarda el movimiento antes de eliminarlo de la lista
                            val updatedList = movimientos.toMutableList().also { it.removeAt(index) }
                            movimientos = updatedList // Actualiza el estado Composable

                            // Guarda la lista actualizada en el archivo en un Coroutine
                            CoroutineScope(Dispatchers.IO).launch {
                                val file = File(context.filesDir, "movimientos.txt") // <-- Reescribe el archivo común
                                file.writeText(updatedList.joinToString("\n") {
                                    "${it.tipo}|${it.descripcion}|${it.monto}|${it.fecha}" // Escribe en el nuevo formato
                                })

                                // --- Lógica para actualizar SaldoDataStore al eliminar ---
                                // Esto es crucial para mantener el saldo total consistente.
                                val saldoActual = SaldoDataStore(context).getSaldo.first() // Lee el saldo actual

                                val nuevoSaldoTotal = when (movimientoEliminado.tipo) {
                                    "gasto" -> saldoActual + movimientoEliminado.monto // Si eliminas un gasto, el saldo debe aumentar
                                    "saldo" -> saldoActual - movimientoEliminado.monto // Si eliminas un saldo, el saldo debe disminuir
                                    else -> saldoActual // No debería pasar, pero por seguridad
                                }
                                SaldoDataStore(context).saveSaldo(nuevoSaldoTotal) // Guarda el nuevo saldo

                                // Mostrar mensaje de éxito en el hilo principal
                                launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Movimiento eliminado y saldo actualizado", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Movimiento", tint = Color.White) // Icono blanco
                        }
                    }
                    Divider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.5f)) // Separador entre elementos
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón para ir a Alertas
            Button(
                onClick = {
                    // Calcula el total de *solo los gastos* para pasarlo a Alertas
                    val totalGastos = movimientos.filter { it.tipo == "gasto" }.sumOf { it.monto }
                    onNavigateToAlertas(totalGastos) // Llama a la función pasada como parámetro

                    // Revisa si esta línea es necesaria o si querías guardar algo diferente aquí.
                    // La lógica de guardar el total de gastos aquí parece redundante con el SaldoDataStore.
                    // CoroutineScope(Dispatchers.IO).launch {
                    // saldoDataStore.saveSaldo(totalGastos) // Revisa esta lógica
                    // }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Ir a Alertas")
            }

            Spacer(Modifier.height(8.dp))

            // Botón para borrar el archivo completo (¡Con cuidado con este!)
            Button(
                onClick = {
                    borrarArchivoMovimientos() // Llama a la función de borrado
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Borrar historial de movimientos")
            }
        }
    }
}

// --- Función @Preview para TablaGastosScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "Historial Movimientos Preview")
@Composable
fun TablaGastosScreenPreview() { // Mantenemos el nombre para el preview, aunque la pantalla muestre más
    MaterialTheme { // Envuelve con el tema Material 3
        val navController = rememberNavController()

        // Proporcionamos una función lambda de prueba para onNavigateToAlertas
        val previewOnNavigateToAlertas: (Double) -> Unit = { total ->
            println("Preview: Navegando a Alertas con total de gastos: $total")
        }

        // --- Mockeamos datos de movimientos para el preview ---
        // Inicializamos el estado 'movimientos' con datos de prueba.
        val previewMovimientos = remember { mutableStateOf(listOf(
            Movimiento("gasto", "Café", 5000.0, "17/05/2025"),
            Movimiento("saldo", "Nómina", 1500000.0, "18/05/2025"),
            Movimiento("gasto", "Almuerzo", 12000.0, "18/05/2025"),
            Movimiento("saldo", "Regalo", 50000.0, "19/05/2025")
        )) }


        // Llama a la Composable que queremos previsualizar
        // En el preview, no necesitamos la lógica de LaunchedEffect que lee del archivo.
        // Solo necesitamos pasar los datos mockeados al estado local 'movimientos'.
        // Como TablaGastosScreen instancia 'movimientos' internamente, no podemos pasar
        // directamente la lista mockeada. Esto es una limitación de la estructura actual.
        // Para un preview más completo con datos mockeados en la lista, sería mejor
        // que TablaGastosScreen recibiera la lista de movimientos como parámetro.

        // Sin modificar TablaGastosScreen para aceptar la lista, el preview mostrará la lista vacía.
        // Sin embargo, la estructura general y la cabecera se verán.
        // Si modificas TablaGastosScreen para recibir List<Movimiento>, podrías hacer esto en el preview:
        // TablaGastosScreen(navController = navController, onNavigateToAlertas = previewOnNavigateToAlertas, movimientos = previewMovimientos.value)

        // Manteniendo la firma actual de TablaGastosScreen:
        TablaGastosScreen(
            navController = navController,
            onNavigateToAlertas = previewOnNavigateToAlertas
        )
        // Nota: Este preview mostrará la pantalla con la lista VACÍA,
        // porque el LaunchedEffect no lee un archivo real en el preview.
        // Para ver datos, la mejor práctica es modificar TablaGastosScreen para aceptar la lista.
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