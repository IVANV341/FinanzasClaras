package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat

// --- Importaciones de iconos y lazy composables (¡LIMPIADAS: NO DUPLICADAS!) ---
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed


// --- Nueva Clase de Datos para Movimiento (Gasto o Saldo) ---
data class Movimiento(
    val tipo: String, // "gasto" o "saldo"
    val descripcion: String,
    val monto: Double,
    val fecha: String // En formato "dd/MM/yyyy"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ¡FIRMA ACTUALIZADA! Añadido selectedCurrency: String
fun TablaGastosScreen (navController: NavController, onNavigateToAlertas: (Double) -> Unit, selectedCurrency: String){
    val context = LocalContext.current

    var movimientos by remember { mutableStateOf(listOf<Movimiento>()) }

    // Función para borrar el archivo de movimientos
    fun borrarArchivoMovimientos() {
        val file = File(context.filesDir, "movimientos.txt")
        if (file.exists()) {
            file.delete()
            movimientos = emptyList() // Limpiar la lista en memoria
            Toast.makeText(context, "Historial de movimientos borrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No existe historial de movimientos", Toast.LENGTH_SHORT).show()
        }
    }

    // LaunchedEffect para cargar datos desde el archivo movimientos.txt
    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "movimientos.txt")
        if (file.exists()) {
            val lines = file.readLines()
                .filter { it.isNotBlank() }
                .map { it.trim() }
            movimientos = lines.mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size == 4) {
                    val tipo = parts[0].trim()
                    val desc = parts[1].trim()
                    val monto = parts[2].trim().toDoubleOrNull()
                    val fecha = parts[3].trim()
                    if (monto != null && (tipo == "gasto" || tipo == "saldo")) {
                        Movimiento(tipo, desc, monto, fecha)
                    } else null
                } else null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Movimientos", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Encabezados de la tabla
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Tipo",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    "Descripción",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
                Text(
                    "Monto",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(0.7f).padding(start = 4.dp)
                )
                Text(
                    "Fecha",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(0.7f).padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            // Formato de moneda para mostrar el monto: ¡AHORA USA selectedCurrency!
            val locale = when (selectedCurrency) {
                "USD" -> Locale("en", "US")
                "EUR" -> Locale("es", "ES")
                "COP" -> Locale("es", "CO")
                else -> Locale.getDefault()
            }
            val format = NumberFormat.getCurrencyInstance(locale)

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
                            movimiento.tipo.capitalize(Locale.getDefault()),
                            modifier = Modifier.weight(0.5f).padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(movimiento.descripcion, modifier = Modifier.weight(1f).padding(end = 4.dp), color = MaterialTheme.colorScheme.onBackground)

                        // Muestra el monto, quizás con color diferente según el tipo
                        val montoColor = when (movimiento.tipo) {
                            "gasto" -> MaterialTheme.colorScheme.error
                            "saldo" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onBackground
                        }
                        Text(
                            format.format(movimiento.monto),
                            modifier = Modifier.weight(0.7f).padding(end = 4.dp),
                            color = montoColor,
                            fontWeight = FontWeight.Bold
                        )

                        Text(movimiento.fecha, modifier = Modifier.weight(0.7f).padding(end = 4.dp), color = MaterialTheme.colorScheme.onBackground)

                        // Botón de eliminar
                        IconButton(onClick = {
                            val movimientoEliminado = movimientos[index]
                            val updatedList = movimientos.toMutableList().also { it.removeAt(index) }
                            movimientos = updatedList

                            CoroutineScope(Dispatchers.IO).launch {
                                val file = File(context.filesDir, "movimientos.txt")
                                file.writeText(updatedList.joinToString("\n") {
                                    "${it.tipo}|${it.descripcion}|${it.monto}|${it.fecha}"
                                })

                                // Asegúrate de que SaldoDataStore esté importado o accesible
                                val saldoActual = co.edu.unab.overa32.finanzasclaras.SaldoDataStore(context).getSaldo.first()
                                val nuevoSaldoTotal = when (movimientoEliminado.tipo) {
                                    "gasto" -> saldoActual + movimientoEliminado.monto
                                    "saldo" -> saldoActual - movimientoEliminado.monto
                                    else -> saldoActual
                                }
                                co.edu.unab.overa32.finanzasclaras.SaldoDataStore(context).saveSaldo(nuevoSaldoTotal)

                                launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Movimiento eliminado y saldo actualizado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Movimiento", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón para ir a Alertas
            Button(
                onClick = {
                    val totalGastos = movimientos.filter { it.tipo == "gasto" }.sumOf { it.monto }
                    onNavigateToAlertas(totalGastos)
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

            // Botón para borrar el archivo completo
            Button(
                onClick = {
                    borrarArchivoMovimientos()
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
fun TablaGastosScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()

        val previewOnNavigateToAlertas: (Double) -> Unit = { total ->
            println("Preview: Navegando a Alertas con total de gastos: $total")
        }

        TablaGastosScreen(
            navController = navController,
            onNavigateToAlertas = previewOnNavigateToAlertas,
            selectedCurrency = "COP" // <-- ¡AÑADIDO PARA EL PREVIEW!
        )
    }
}