// Este archivo define la pantalla de bienvenida (`SplashScreen`) de la aplicación.
// Muestra el logo de la aplicación durante un período de tiempo determinado y,
// al finalizar, navega automáticamente a la siguiente pantalla (usualmente la principal
// o la de inicio de sesión), eliminándose de la pila de navegación para que el usuario
// no pueda volver a ella.



package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource // Para cargar imágenes desde drawables
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay // Para el retardo

// ¡IMPORTANTE! Asegúrate de tener un logo en tu carpeta drawable.
// Puedes usar R.drawable.ic_launcher_foreground como placeholder si no tienes uno.
// Si tienes un logo personalizado, por ejemplo, "mi_logo.png", ponlo en res/drawable/mi_logo.png
// y cambia R.drawable.ic_launcher_foreground por R.drawable.mi_logo

@Composable
fun SplashScreen(navController: NavHostController) {
    // LaunchedEffect se ejecuta una vez cuando el composable entra en composición
    LaunchedEffect(key1 = true) {
        delay(3000L) // Retardo de 3 segundos (3000 milisegundos)
        navController.navigate("main") { // Navega a la pantalla principal
            popUpTo("splashScreen") { inclusive = true } // Elimina SplashScreen de la pila de navegación
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary), // O cualquier color de fondo que prefieras
        contentAlignment = Alignment.Center
    ) {
        // Muestra tu logo aquí
        Image(
            painter = painterResource(id = R.drawable.logofinanzasclaras), // <-- ¡CONFIRMADO! Esta línea es correcta para .png
            contentDescription = "Logo de la aplicación Finanzas Claras",
            modifier = Modifier.size(200.dp) // Ajusta el tamaño según sea necesario
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(navController = rememberNavController())
    }
}
