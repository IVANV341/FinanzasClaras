package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import co.edu.unab.overa32.finanzasclaras.AlertThresholdsRepository
import co.edu.unab.overa32.finanzasclaras.AppDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertScreen(
    navController: NavHostController,
    alertsViewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModelFactory(
            saldoDataStore = SaldoDataStore(LocalContext.current),
            alertThresholdsRepository = AlertThresholdsRepository(AppDatabase.getDatabase(LocalContext.current).alertThresholdDao()),
            applicationContext = LocalContext.current.applicationContext
        )
    )
) {
    var amountText by remember { mutableStateOf("") }
    // ¡NUEVO ESTADO! Para el tipo de alerta seleccionado
    var selectedAlertType by remember { mutableStateOf(AlertType.LOW_BALANCE) } // Por defecto, Saldo Bajo
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nueva Alerta", color = TextColorWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextColorWhite)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            // ¡NUEVO! Pasamos el tipo de alerta seleccionado al ViewModel
                            alertsViewModel.addAlertThreshold(amount, selectedAlertType)
                            navController.popBackStack() // Volver a la pantalla de alertas
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Por favor, introduce un monto válido y mayor a cero.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Guardar Alerta", tint = TextColorWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(PurpleBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = amountText,
                onValueChange = { newValue ->
                    // Permite solo números y un punto decimal
                    amountText = newValue.filter { it.isDigit() || it == '.' }
                },
                label = { Text("Monto del Umbral", color = TextColorWhite) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextColorWhite,
                    unfocusedTextColor = TextColorWhite,
                    focusedBorderColor = TextColorWhite,
                    unfocusedBorderColor = TextColorGray,
                    focusedLabelColor = TextColorWhite,
                    unfocusedLabelColor = TextColorGray,
                    cursorColor = TextColorWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ¡NUEVA SECCIÓN! Selector para el tipo de alerta (Saldo Alto/Bajo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RadioButton para Saldo Bajo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedAlertType == AlertType.LOW_BALANCE,
                        onClick = { selectedAlertType = AlertType.LOW_BALANCE }
                    )
                    Text("Saldo Bajo", color = TextColorWhite)
                }

                // RadioButton para Saldo Alto
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedAlertType == AlertType.HIGH_BALANCE,
                        onClick = { selectedAlertType = AlertType.HIGH_BALANCE }
                    )
                    Text("Saldo Alto", color = TextColorWhite)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddAlertScreenPreview() {
    MaterialTheme {
        AddAlertScreen(navController = rememberNavController())
    }
}