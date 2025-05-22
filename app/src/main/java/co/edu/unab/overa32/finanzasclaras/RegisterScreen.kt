// Este archivo define la pantalla `RegisterScreen`, que permite a los usuarios
// crear una nueva cuenta en la aplicación. Solicita un correo electrónico y una
// contraseña (con confirmación), y utiliza `AuthViewModel` para manejar el proceso
// de registro con Firebase. Incluye una interfaz de usuario limpia con campos de
// texto y un botón de registro, además de un fondo animado con ondas y la opción
// de volver a la pantalla de inicio de sesión.




package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(FirebaseAuth.getInstance()))
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val authState by authViewModel.authState.collectAsState()

    // Manejar errores o estados de carga
    LaunchedEffect(authState.error) {
        authState.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Error al registrar: $it",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)) // Fondo púrpura
    ) {
        RegisterBackgroundWaves()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Registrarse", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF673AB7)) // Fondo púrpura
                )
            },
            containerColor = Color.Transparent // Make Scaffold background transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            authViewModel.registerUser(email, password)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Las contraseñas no coinciden.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    enabled = !authState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Color Verde
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Registrarse", fontSize = 18.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("¿Ya tienes cuenta? Inicia sesión.", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RegisterBackgroundWaves() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val waveColor = Color(0xFFE0BBE4).copy(alpha = 0.3f) // Light purple with opacity
        val lineColor = Color.White.copy(alpha = 0.1f)     // White with opacity

        // Ondas superiores
        val wavePathTop = Path().apply {
            moveTo(0f, 0f)
            for (i in 0..width.toInt() step 80) {
                val x = i.toFloat()
                val y = (sin(x * 0.02) * 15).toFloat() + height * 0.1f
                lineTo(x, y)
            }
            lineTo(width, 0f)
            close()
        }
        drawPath(path = wavePathTop, color = waveColor)
        drawPath(path = wavePathTop, color = lineColor, style = Stroke(width = 1f))

        // Ondas inferiores
        val wavePathBottom = Path().apply {
            moveTo(0f, height)
            for (i in 0..width.toInt() step 80) {
                val x = i.toFloat()
                val y = height - ((sin(x * 0.02 + 0.5) * 20).toFloat() + height * 0.15f)
                lineTo(x, y)
            }
            lineTo(width, height)
            close()
        }
        drawPath(path = wavePathBottom, color = waveColor)
        drawPath(path = wavePathBottom, color = lineColor, style = Stroke(width = 1f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(navController = rememberNavController())
    }
}
