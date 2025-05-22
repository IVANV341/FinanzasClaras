// Este archivo define la clase `BalanceMonitor`, la cual opera en segundo plano para
// supervisar constantemente el saldo del usuario en relación con los umbrales de alerta configurados.
// Utiliza `CoroutineScope` y `Flow.combine` para reaccionar a cambios en el saldo o las alertas,
// y dispara notificaciones a través de `NotificationHelper` cuando se cumplen las condiciones de un umbral.
// Es crucial para garantizar que las alertas funcionen incluso cuando la aplicación no está en primer plano.



package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// Esta clase se encarga de monitorear el saldo y disparar alertas en segundo plano
class BalanceMonitor(
    private val saldoDataStore: SaldoDataStore,
    private val alertThresholdsRepository: AlertThresholdsRepository,
    private val applicationContext: Context
) {
    // Usamos un CoroutineScope para manejar las corrutinas de monitoreo.
    // SupervisorJob permite que una corrutina falle sin cancelar otras.
    private val monitorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Función para iniciar el monitoreo
    fun startMonitoring() {
        monitorScope.launch {
            // Combinamos el saldo del SaldoDataStore y los umbrales del AlertThresholdsRepository
            // Este Flow se ejecutará siempre que el saldo o los umbrales cambien
            saldoDataStore.getSaldo.combine(alertThresholdsRepository.getAllAlertThresholds()) { saldo, umbrales ->
                // Este bloque se ejecutará cada vez que haya un nuevo saldo o lista de umbrales
                Pair(saldo, umbrales) // Empaquetamos ambos en un Pair
            }.collect { (currentSaldo, umbralesAlerta) ->
                // Llamamos a la lógica de verificación de alertas
                // Esta lógica ahora está en el monitor, no solo en el ViewModel de la pantalla
                checkAlertThresholds(currentSaldo, umbralesAlerta)
            }
        }
    }

    // Función para detener el monitoreo (cuando la aplicación se cierre, por ejemplo)
    fun stopMonitoring() {
        monitorScope.cancel() // Cancela todas las corrutinas en este scope
    }

    // La lógica de verificación de alertas, ahora centralizada aquí
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
                        alert.type
                    )
                    println("¡ALERTA EN SEGUNDO PLANO DISPARADA! Saldo de $currentSaldo ha ${if (alert.type == AlertType.HIGH_BALANCE) "alcanzado o superado" else "caído por debajo de"} el umbral de ${alert.amount} (Tipo: ${alert.type})")

                    // Lógica para evitar spam de notificaciones.
                    // Para evitar que se dispare cada vez que el saldo cambie y cumpla el umbral,
                    // podríamos añadir aquí una lógica para deshabilitar la alerta temporalmente
                    // o marcarla como "disparada" en la base de datos (ROOM).
                    // Esto requeriría añadir un campo adicional a AlertThresholdEntity,
                    // y luego actualizar la alerta en el repositorio aquí:
                    /*
                    // Ejemplo para deshabilitar la alerta una vez disparada
                    monitorScope.launch(Dispatchers.IO) { // Asegurarse de que la actualización es en un hilo de IO
                        alertThresholdsRepository.updateAlertThreshold(alert.copy(isEnabled = false))
                    }
                    */
                }
            }
        }
    }
}
