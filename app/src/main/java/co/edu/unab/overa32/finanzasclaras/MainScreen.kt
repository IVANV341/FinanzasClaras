package co.edu.unab.overa32.finanzasclaras // *** IMPORTANTE: Reemplaza con el nombre correcto de tu paquete ***

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
// ELIMINAR: import androidx.compose.foundation.border // No se usa en esta Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* // Usamos Material 3 components
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// ELIMINAR: import androidx.compose.ui.graphics.Color // ¡Ya no lo necesitamos si no usamos colores fijos!
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // ¡Importante para @Preview!
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// ELIMINAR O COMENTAR: import androidx.navigation.NavController // No usado directamente en esta firma de PantallaPrincipalUI
import androidx.navigation.compose.rememberNavController // ¡Importante para el Preview!
import androidx.compose.material3.MaterialTheme // ¡Importante para MaterialTheme.colorScheme!
import java.text.NumberFormat
import java.util.Locale

// --- ESTA ES LA DEFINICIÓN DE TU PANTALLA PRINCIPAL ---
// Ahora contiene todas las funcionalidades de botones, saldo adaptable, y moneda.
@SuppressLint("ContextCastToActivity")
@Composable
fun PantallaPrincipalUI(
    saldoTotal: Double, // Dato que recibe para mostrar
    onAddExpenseClick: () -> Unit, // Acción para Añadir Gasto
    onTablaGastosClick: () -> Unit, // Acción para Tabla de gastos
    onConfiguracionClick: () -> Unit, // Acción para Configuracion
    onPreguntasClick: () -> Unit, // Acción para botón "preguntas" (IA)
    onAddSaldoClick: () -> Unit, // Acción para botón "Añadir Saldo"
    onSalirClick: () -> Unit, // Acción para Salir
    selectedCurrency: String // PARÁMETRO PARA LA MONEDA
) {
    val activity = (LocalContext.current as? Activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // FONDO ADAPTABLE
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // --- Botón "Añadir Gasto" ---
        Button(
            onClick = onAddExpenseClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Añadir Gasto", fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))

        // --- Sección para mostrar el Total ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // FONDO TARJETA ADAPTABLE
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Total Gastado:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // COLOR TEXTO ADAPTABLE
                )
                Spacer(Modifier.height(4.dp))

                // Formatear el saldo: ¡AHORA USA selectedCurrency!
                val locale = when (selectedCurrency) {
                    "USD" -> Locale("en", "US")
                    "EUR" -> Locale("es", "ES")
                    "COP" -> Locale("es", "CO")
                    else -> Locale.getDefault()
                }
                val format = NumberFormat.getCurrencyInstance(locale)
                val saldoFormateado = format.format(saldoTotal)

                Text(
                    text = saldoFormateado,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // COLOR TEXTO ADAPTABLE
                )
            }
        }

        // *** Botón "Añadir Saldo" ***
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = onAddSaldoClick, // Usa la nueva lambda
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary, // COLOR BOTÓN ADAPTABLE
                contentColor = MaterialTheme.colorScheme.onTertiary // COLOR TEXTO ADAPTABLE
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Añadir Saldo", fontSize = 18.sp)
        }

        Spacer(Modifier.height(50.dp))

        // --- Botón "Tabla de gastos" ---
        Button(
            onClick = onTablaGastosClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary, // COLOR BOTÓN ADAPTABLE
                contentColor = MaterialTheme.colorScheme.onSecondary // COLOR TEXTO ADAPTABLE
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Tabla de gastos", fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))

        // Botón "preguntas"
        Button(
            onClick = onPreguntasClick, // Usa la nueva lambda
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer, // COLOR BOTÓN ADAPTABLE
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer // COLOR TEXTO ADAPTABLE
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("preguntas", fontSize = 18.sp)
        }

        Spacer(Modifier.height(30.dp))

        // Botón "Configuracion"
        Button(
            onClick = onConfiguracionClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer, // COLOR BOTÓN ADAPTABLE
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer // COLOR TEXTO ADAPTABLE
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Configuracion", fontSize = 18.sp)
        }

        Spacer(Modifier.weight(1f))

        // --- Botón "Salir" ---
        Button(
            onClick = onSalirClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Salir", fontSize = 18.sp)
        }
    }
}


// --- Preview (ACTUALIZADO CON LOS NUEVOS PARÁMETROS) ---
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        PantallaPrincipalUI(
            saldoTotal = 1850000.99,
            // Proporciona lambdas vacías para el preview
            onAddExpenseClick = { /* Preview click */ },
            onTablaGastosClick = { /* Preview click */ },
            onConfiguracionClick = { /* Preview click */ },
            onPreguntasClick = { /* Preview click */ }, // Nueva lambda para preview
            onAddSaldoClick = { /* Preview click */ }, // Nueva lambda para preview
            onSalirClick = { /* Preview click */ },
            selectedCurrency = "COP" // ¡AÑADE ESTO AL PREVIEW!
        )
    }
}