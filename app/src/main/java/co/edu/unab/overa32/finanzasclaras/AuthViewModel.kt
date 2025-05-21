package co.edu.unab.overa32.finanzasclaras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth // Importa la clase de autenticación de Firebase
import com.google.firebase.auth.FirebaseUser // Para obtener el usuario autenticado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Para esperar el resultado de las tareas de Firebase

// --- 1. Clase para representar el estado de la autenticación ---
data class AuthState(
    val user: FirebaseUser? = null, // El usuario autenticado, o null si no hay
    val isLoading: Boolean = false, // Indica si una operación de autenticación está en curso
    val isAuthenticated: Boolean = false, // True si hay un usuario logueado
    val error: String? = null // Mensaje de error si ocurre uno
)

// --- 2. ViewModel de Autenticación ---
class AuthViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Observar cambios en el estado de autenticación de Firebase
        firebaseAuth.addAuthStateListener { auth ->
            _authState.value = _authState.value.copy(
                user = auth.currentUser,
                isAuthenticated = auth.currentUser != null,
                isLoading = false, // No hay operación en curso al inicio
                error = null // Limpiar errores previos al detectar un cambio de estado
            )
        }
    }

    fun registerUser(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                // Si el registro es exitoso, el authStateListener se encargará de actualizar el estado
                _authState.value = _authState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                // Si el login es exitoso, el authStateListener se encargará de actualizar el estado
                _authState.value = _authState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}

// --- 3. ViewModelFactory para AuthViewModel ---
// Es necesario para poder inyectar la instancia de FirebaseAuth
class AuthViewModelFactory(private val firebaseAuth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(firebaseAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}