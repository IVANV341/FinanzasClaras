

// Este archivo contiene la pantalla `AddGastoScreen`, que sirve como un esqueleto
// o marcador de posición para la funcionalidad de añadir nuevos gastos.
// Incluye una `TopAppBar` básica con un botón de retroceso y un texto central
// que indica su propósito, además de un botón simple para volver a la pantalla anterior.
// Se integra con `NavController` y `SaldoDataStore` para futuras implementaciones.



package co.edu.unab.overa32.finanzasclaras // Reemplaza con tu paquete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Usamos Material 3 components
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import co.edu.unab.overa32.finanzasclaras.SaldoDataStore // Necesario si AddGastoScreen interactúa con el saldo
import androidx.compose.runtime.remember // Para el remember de SaldoDataStore en el preview
import androidx.compose.ui.platform.LocalContext // Para LocalContext en el preview

// ESTE ARCHIVO AHORA CONTIENE LA PANTALLA 'AddGastoScreen' (el esqueleto/placeholder).

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Esta es la AddGastoScreen que actúa como esqueleto.
fun AddGastoScreen(navController: NavController, saldoDataStore: SaldoDataStore, selectedCurrency: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Gasto (Esqueleto)", color = MaterialTheme.colorScheme.onPrimary) }, // Título para distinguirlo
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Aquí iría la interfaz para añadir un nuevo gasto. (Moneda: $selectedCurrency)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}

// --- Preview de AddGastoScreen (Esqueleto) ---
@Preview(showBackground = true, showSystemUi = true, name = "Añadir Gasto Esqueleto Preview") // Nombre de preview para distinguirlo
@Composable
fun AddGastoScreenPreview() { // Este preview estará en gastos.kt
    MaterialTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val saldoDataStore = remember { SaldoDataStore(context) }

        AddGastoScreen(
            navController = navController,
            saldoDataStore = saldoDataStore,
            selectedCurrency = "COP" // Puedes cambiar esto para ver el preview con otra moneda
        )
    }
}