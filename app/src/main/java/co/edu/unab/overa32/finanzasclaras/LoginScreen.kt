package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Asegúrate de tener este import
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import co.edu.unab.overa32.finanzasclaras.LoginBackgroundWaves // Importa la función desde su nuevo archivo
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen( // <-- INICIO DE LA FUNCIÓN LoginScreen
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(FirebaseAuth.getInstance()))
) { // <-- CIERRE DE LA FUNCIÓN LoginScreen
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val authState by authViewModel.authState.collectAsState()

    // Manejar la navegación después de un login exitoso
    LaunchedEffect(authState.isAuthenticated) { // <-- INICIO LaunchedEffect
        if (authState.isAuthenticated) { // <-- INICIO if
            navController.navigate("main") { // <-- INICIO navigate
                popUpTo("loginScreen") { inclusive = true }
            } // <-- CIERRE navigate
        } // <-- CIERRE if
    } // <-- CIERRE LaunchedEffect

    // Mostrar mensajes de error o carga
    LaunchedEffect(authState.error) { // <-- INICIO LaunchedEffect
        authState.error?.let { // <-- INICIO let
            scope.launch { // <-- INICIO launch
                snackbarHostState.showSnackbar(
                    message = "Error: $it",
                    duration = SnackbarDuration.Long
                )
            } // <-- CIERRE launch
        } // <-- CIERRE let
    } // <-- CIERRE LaunchedEffect

    Box( // <-- INICIO Box (Contenedor principal de fondo)
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { // <-- CIERRE Box (Contenedor principal de fondo)
        // Fondo con ondas abstractas (inspirado en el submarino)
        LoginBackgroundWaves()

        Scaffold( // <-- INICIO Scaffold (Contenedor con TopAppBar y SnackbarHost)
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { // <-- INICIO topBar
                TopAppBar(
                    title = { Text("Iniciar Sesión", color = MaterialTheme.colorScheme.onSurface) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                )
            } // <-- CIERRE topBar
            , // <-- IMPORTANTE: La coma va aquí si el bloque topBar está cerrado
            containerColor = Color.Transparent // Fondo transparente para ver el Box de abajo
        ) { paddingValues -> // <-- INICIO el bloque de contenido de Scaffold
            Column( // <-- INICIO Column (Contenido central)
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { // <-- CIERRE Column (Contenido central)
                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.logofinanzasclaras), // Tu logo
                    contentDescription = "Logo de la aplicación",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "Bienvenido de nuevo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { authViewModel.loginUser(email, password) },
                    enabled = !authState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Iniciar Sesión", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("registerScreen") }) {
                    Text("¿No tienes cuenta? Regístrate aquí.", color = MaterialTheme.colorScheme.primary)
                }
            } // <-- CIERRE de la Columna principal de contenido.
        } // <-- CIERRE del Scaffold.
    } // <-- CIERRE del Box principal.
} // <-- CIERRE de la función LoginScreen.

// Composable para el fondo abstracto (ondas)


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() { // <-- INICIO LoginScreenPreview
    MaterialTheme { // <-- INICIO MaterialTheme
        LoginScreen(navController = rememberNavController())
    } // <-- CIERRE MaterialTheme
} // <-- CIERRE LoginScreenPreview