// Este archivo define la interfaz de usuario principal de la aplicación, `PantallaPrincipalUI`.
// Muestra el saldo total del usuario, así como botones para navegar a las funcionalidades clave
// como añadir gastos, ver la tabla de gastos, configurar ajustes, acceder al asistente de IA,
// añadir saldo y salir de la aplicación. El diseño utiliza temas de Material Design 3
// y adaptabilidad al modo oscuro, con un fondo degradado que evoca un "tema submarino".





package co.edu.unab.overa32.finanzasclaras

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@SuppressLint("ContextCastToActivity")
@Composable
fun PantallaPrincipalUI(
    saldoTotal: Double,
    onAddExpenseClick: () -> Unit,
    onTablaGastosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onPreguntasClick: () -> Unit,
    onAddSaldoClick: () -> Unit,
    onSalirClick: () -> Unit,
    selectedCurrency: String
) {
    val activity = (LocalContext.current as? Activity)

    // Define submarine theme colors - using isSystemInDarkTheme() instead of color comparison
    val isDarkTheme = isSystemInDarkTheme()
    val primaryBlue = if (isDarkTheme) Color(0xFF1E88E5) else Color(0xFF2196F3)
    val deepBlue = if (isDarkTheme) Color(0xFF0D47A1) else Color(0xFF1565C0)
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Create gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            deepBlue.copy(alpha = 0.3f)
        ),
        startY = 0f,
        endY = 2000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Submarine theme decorative elements
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.TopCenter)
                .alpha(0.1f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, deepBlue),
                        startY = 0f,
                        endY = 200f
                    )
                )
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // App Title
            Text(
                text = "Finanzas Claras",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(40.dp))

            // Total Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = surfaceColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Gastado",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    // Formatear el saldo con la moneda seleccionada
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
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryBlue
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Main Action Buttons
            Button(
                onClick = onAddExpenseClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Añadir Gasto", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onAddSaldoClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Añadir Saldo", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onTablaGastosClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tabla de Gastos", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))

            // Secondary Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPreguntasClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Preguntas", fontSize = 16.sp)
                }

                Spacer(Modifier.width(16.dp))

                OutlinedButton(
                    onClick = onConfiguracionClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Configuración", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.weight(1f))

            // Exit Button
            Button(
                onClick = onSalirClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salir", fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        PantallaPrincipalUI(
            saldoTotal = 1850000.99,
            onAddExpenseClick = { /* Preview click */ },
            onTablaGastosClick = { /* Preview click */ },
            onConfiguracionClick = { /* Preview click */ },
            onPreguntasClick = { /* Preview click */ },
            onAddSaldoClick = { /* Preview click */ },
            onSalirClick = { /* Preview click */ },
            selectedCurrency = "COP"
        )
    }
}