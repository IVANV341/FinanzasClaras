package co.edu.unab.overa32.finanzasclaras // O el subpaquete donde esté este archivo

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* // Usamos Material 3 components
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview // ¡Importante para @Preview!
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // ¡Importante para el Preview!
import java.text.NumberFormat
import java.util.Locale

// --- Tu Función Composable Principal Modificada ---
@SuppressLint("ContextCastToActivity")
@Composable
fun PantallaPrincipalUI(saldoTotal: Double, navController: NavController) {
    val activity = (LocalContext.current as? Activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // --- Botón "Añadir Gasto" ---
        Button(
            onClick = {
                navController.navigate("addGasto") // Navegar a la pantalla Añadir Gasto
            },
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

        Spacer(Modifier.height(50.dp))

        // --- Sección para mostrar el Total ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Total Gastado:", // Quizás quieras cambiar esto a "Saldo Actual" o "Total"
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))

                // Formatear el saldo
                val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                val saldoFormateado = format.format(saldoTotal)

                Text(
                    text = saldoFormateado,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // O un color verde si es positivo, rojo si es negativo, etc.
                )
            }
        }

        // *** Aquí añadimos el nuevo botón "Añadir Saldo" ***
        Spacer(Modifier.height(30.dp)) // Espacio después de la tarjeta del Total

        Button(
            onClick = {
                navController.navigate("addSaldo") // TODO: Define la ruta para Añadir Saldo
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4DB6AC), // Un color verde azulado (Teal 400 de Material)
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Añadir Saldo", fontSize = 18.sp)
        }

        // *** Espacio después del botón "Añadir Saldo" ***
        Spacer(Modifier.height(50.dp)) // Puedes ajustar este espacio si lo deseas

        // --- Botones Secundarios existentes (Ahora debajo del nuevo botón) ---

        // Botón "Tabla de gastos"
        Button(
            onClick = {
                navController.navigate("tablaGastos") // Navegar a Tabla de gastos
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Tabla de gastos", fontSize = 18.sp)
        }

        Spacer(Modifier.height(30.dp))

        // Botón "preguntas"
        Button(
            onClick = {
                navController.navigate("aiScreen") // Navegar a la pantalla de IA
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF64B5F6), // Azul claro
                contentColor = Color.White
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
            onClick = {
                navController.navigate("ajustes") // Navegar a Configuración
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF81C784), // Verde claro
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Configuracion", fontSize = 18.sp)
        }

        Spacer(Modifier.weight(1f)) // Empuja el botón Salir hacia abajo

        // --- Botón "Salir" ---
        Button(
            onClick = {
                activity?.finishAffinity() // Sale de la aplicación
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error, // Rojo de error
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

// --- Función @Preview (Se mantiene igual, ya que PantallaPrincipalUI ahora incluye el nuevo botón) ---
@Preview(showBackground = true, showSystemUi = true, name = "Pantalla Principal Preview")
@Composable
fun PantallaPrincipalUIPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        PantallaPrincipalUI(
            saldoTotal = 1850000.99, // Puedes ajustar el saldo de ejemplo para ver el nuevo botón
            navController = navController
        )
    }
}

// ... (Data classes u otro código si lo tienes) ...