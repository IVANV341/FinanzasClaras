package co.edu.unab.overa32.finanzasclaras

import android.content.Context // ¡IMPORTANTE! Nuevo import
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine // Nuevo import

// --- 1. Estado de la UI para la pantalla de Alertas ---
data class AlertasUiState(
    val saldoActual: Double = 0.0,
    val umbralesAlerta: List<AlertThreshold> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// --- 2. ViewModel ---
class AlertsViewModel(
    private val saldoDataStore: SaldoDataStore,
    private val alertThresholdsRepository: AlertThresholdsRepository, // ¡AÑADIDO!
    private val applicationContext: Context // ¡AÑADIDO! Para acceder al contexto para notificaciones
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertasUiState())
    val uiState: StateFlow<AlertasUiState> = _uiState.asStateFlow()

    init {
        // Inicializa la carga de datos cuando el ViewModel es creado
        collectData()
    }

    private fun collectData() {
        viewModelScope.launch {
            // Combinamos el saldo del SaldoDataStore y los umbrales del AlertThresholdsRepository
            saldoDataStore.getSaldo.combine(alertThresholdsRepository.getAllAlertThresholds()) { saldo, umbrales ->
                // Actualizamos el estado con los nuevos valores
                _uiState.value.copy(
                    saldoActual = saldo,
                    umbralesAlerta = umbrales,
                    isLoading = false // Ya hemos cargado los datos iniciales
                )
            }.collect { updatedState ->
                _uiState.value = updatedState
                // Cuando el estado se actualiza, verificamos las alertas
                checkAlertThresholds(updatedState.saldoActual, updatedState.umbralesAlerta)
            }
        }
    }

    private fun checkAlertThresholds(currentSaldo: Double, thresholds: List<AlertThreshold>) {
        thresholds.forEach { alert ->
            if (alert.isEnabled) { // Solo si la alerta está activada
                val shouldTrigger = when (alert.type) {
                    AlertType.HIGH_BALANCE -> currentSaldo >= alert.amount // Se dispara si el saldo es MAYOR O IGUAL
                    AlertType.LOW_BALANCE -> currentSaldo <= alert.amount  // Se dispara si el saldo es MENOR O IGUAL
                }

                if (shouldTrigger) {
                    // ¡Disparar la notificación real!
                    NotificationHelper.showBalanceAlertNotification(
                        applicationContext,
                        alert.id,
                        currentSaldo,
                        alert.amount,
                        alert.type // ¡NUEVO! Pasamos el tipo de alerta a la notificación
                    )
                    println("¡ALERTA REAL DISPARADA! Saldo de $currentSaldo ha ${if (alert.type == AlertType.HIGH_BALANCE) "alcanzado o superado" else "caído por debajo de"} el umbral de ${alert.amount} (Tipo: ${alert.type})")

                    // Opcional: Lógica para evitar spam de notificaciones.
                    // Si quieres que se dispare UNA VEZ hasta que el usuario la reinicie:
                    /*
                    viewModelScope.launch {
                        val updatedAlert = alert.copy(isEnabled = false) // Deshabilitar la alerta una vez disparada
                        alertThresholdsRepository.updateAlertThreshold(updatedAlert)
                    }
                    */
                }
            }
        }
    }


    // --- Funciones para interactuar con los umbrales (llamadas desde la UI) ---

    fun addAlertThreshold(amount: Double, type: AlertType) { // <-- AÑADIDO 'type: AlertType'
        viewModelScope.launch {
            // Room generará el ID automáticamente (id = 0L en AlertThresholdEntity)
            // ¡CORREGIDO! Ahora se pasa el 'type' al constructor de AlertThreshold
            val newThreshold = AlertThreshold(id = 0L, amount = amount, isEnabled = true, type = type) // <-- AÑADIDO 'type = type'
            alertThresholdsRepository.insertAlertThreshold(newThreshold)
        }
    }

    fun toggleAlertThreshold(alertId: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            // Busca el umbral actual para obtener todos sus datos (excepto el ID)
            val currentThreshold = _uiState.value.umbralesAlerta.find { it.id == alertId }
            if (currentThreshold != null) {
                // Crea una copia con el estado actualizado y la guarda
                alertThresholdsRepository.updateAlertThreshold(currentThreshold.copy(isEnabled = isEnabled))
            }
        }
    }

    fun deleteAlertThreshold(alertId: Long) {
        viewModelScope.launch {
            alertThresholdsRepository.deleteAlertThreshold(alertId)
        }
    }
}

// --- 3. ViewModelFactory para instanciar el ViewModel con dependencias ---
class AlertsViewModelFactory(
    private val saldoDataStore: SaldoDataStore,
    private val alertThresholdsRepository: AlertThresholdsRepository, // ¡AÑADIDO!
    private val applicationContext: Context // ¡AÑADIDO!
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pasa todas las dependencias al constructor de AlertsViewModel
            return AlertsViewModel(saldoDataStore, alertThresholdsRepository, applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}