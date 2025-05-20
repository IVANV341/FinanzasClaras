package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.text.NumberFormat

// Asegúrate de que tu clase SaldoDataStore esté importada o en el mismo paquete
// Asegúrate que SaldoDataStore.MockSaldoDataStore exista en SaldoDataStore.kt


// ESTE ARCHIVO CONTIENE LA PANTALLA 'AddGastoCompletoScreen' (la lógica completa).

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGastoCompletoScreen(navController: NavController, saldoDataStore: SaldoDataStore, selectedCurrency: String){
    val context = LocalContext.current

    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    // Formato de moneda: ¡AHORA USA selectedCurrency!
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
                title = { Text("Añadir Gasto Completo", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Añadir Gasto",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
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
                colors = OutlinedTextFieldDefaults.colors(
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
                    if (montoDouble != null && montoDouble > 0 && descripcion.isNotBlank()) {
                        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val lineaMovimiento = "gasto|$descripcion|$montoDouble|$currentDate\n"
                        val file = File(context.filesDir, "movimientos.txt")

                        CoroutineScope(Dispatchers.IO).launch {
                            file.appendText(lineaMovimiento)
                            val saldoActual = SaldoDataStore(context).getSaldo.first()
                            val nuevoSaldoTotal = saldoActual - montoDouble
                            saldoDataStore.saveSaldo(nuevoSaldoTotal)

                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Gasto registrado y saldo actualizado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }

                    } else {
                        val errorMessage = if (montoDouble == null || montoDouble <= 0) {
                            "Por favor ingresa un monto válido mayor a cero."
                        } else {
                            "Por favor completa la descripción."
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Gasto")
            }
        }
    }
} // <--- ¡CIERRE DE LA FUNCIÓN AddGastoCompletoScreen AQUÍ!


// --- PREVIEW PARA ESTA PANTALLA (AddGastoCompletoScreen) ---
// ¡ESTA FUNCIÓN @Preview DEBE ESTAR AL MISMO NIVEL QUE AddGastoCompletoScreen!
@Preview(showBackground = true, showSystemUi = true, name = "Añadir Gasto Completo Preview")
@Composable
fun AddGastoCompletoScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val previewContext = LocalContext.current

        val mockSaldoDataStore = remember {
            object : SaldoDataStore(previewContext) {
                override val getSaldo: kotlinx.coroutines.flow.Flow<Double> = kotlinx.coroutines.flow.flowOf(1000000.0)
                override suspend fun saveSaldo(saldo: Double) {
                    println("Preview Mock: Intentando guardar saldo $saldo")
                }
            }
        }
        AddGastoCompletoScreen(
            navController = navController,
            saldoDataStore = mockSaldoDataStore,
            selectedCurrency = "COP"
        )
    }
}