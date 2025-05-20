package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.TopAppBarDefaults // <-- ¡IMPORTACIÓN AÑADIDA!
import androidx.compose.material3.OutlinedTextFieldDefaults // <-- ¡IMPORTACIÓN AÑADIDA!
import androidx.compose.material3.MaterialTheme // <-- ¡Añadida!
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// ELIMINAR: import androidx.compose.ui.graphics.Color // ¡Ya no se usa Color fijo!
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview

// Necesarias para Coroutines y DataStore/File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first // <-- Importación para obtener el primer valor del Flow
import kotlinx.coroutines.flow.flowOf // <-- Importación para flowOf en el mock del Preview (si usas mock)

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.text.NumberFormat // <-- ¡Añadida!

// Asegúrate de que tu clase SaldoDataStore esté importada o en el mismo paquete
// Asegúrate que SaldoDataStore.MockSaldoDataStore exista en SaldoDataStore.kt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ¡FIRMA ACTUALIZADA! Añadido selectedCurrency: String
fun SaldoScreen(navController: NavController, selectedCurrency: String){
    val context = LocalContext.current
    // Instancia de SaldoDataStore usando el contexto
    val saldoDataStore = remember { SaldoDataStore(context) }

    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    // Formato de moneda para mostrar el saldo: ¡AHORA USA selectedCurrency!
    val locale = when (selectedCurrency) {
        "USD" -> Locale("en", "US")
        "EUR" -> Locale("es", "ES")
        "COP" -> Locale("es", "CO")
        else -> Locale.getDefault()
    }
    val format = NumberFormat.getCurrencyInstance(locale)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Saldo", color = MaterialTheme.colorScheme.onPrimary) }, // Colores adaptados
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary) // Colores adaptados
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary) // Colores adaptados
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background), // Fondo adaptable
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Añadir Saldo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // Color de texto adaptable
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // Colores adaptados
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors( // Colores adaptados
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull()
                    if (montoDouble != null && montoDouble > 0) {
                        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val descMovimiento = if (descripcion.isBlank()) "Ingreso" else descripcion.trim()
                        val lineaMovimiento = "saldo|$descMovimiento|$montoDouble|$currentDate\n"
                        val file = File(context.filesDir, "movimientos.txt")

                        CoroutineScope(Dispatchers.IO).launch {
                            file.appendText(lineaMovimiento)
                            // Asegúrate de que SaldoDataStore esté importado o accesible
                            val saldoActual = co.edu.unab.overa32.finanzasclaras.SaldoDataStore(context).getSaldo.first()
                            val nuevoSaldoTotal = saldoActual + montoDouble
                            co.edu.unab.overa32.finanzasclaras.SaldoDataStore(context).saveSaldo(nuevoSaldoTotal)

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
                    containerColor = MaterialTheme.colorScheme.tertiary, // Colores adaptados
                    contentColor = MaterialTheme.colorScheme.onTertiary // Colores adaptados
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Saldo")
            }
        }
    }
}

// --- Función @Preview para SaldoScreen ---
@Preview(showBackground = true, showSystemUi = true, name = "Saldo Screen Preview")
@Composable
fun SaldoScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val previewContext = LocalContext.current // Necesario para el mock de SaldoDataStore

        // Creamos un mock simple de SaldoDataStore para el preview.
        val mockSaldoDataStore = remember {
            object : SaldoDataStore(previewContext) {
                override val getSaldo: kotlinx.coroutines.flow.Flow<Double> = kotlinx.coroutines.flow.flowOf(500000.0) // Valor de saldo para preview
                override suspend fun saveSaldo(saldo: Double) {
                    println("Preview Mock: Intentando guardar saldo $saldo")
                }
            }
        }

        SaldoScreen(
            navController = navController,
            selectedCurrency = "COP" // <-- ¡AÑADIDO PARA EL PREVIEW!
        )
    }
}