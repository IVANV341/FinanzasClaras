package co.edu.unab.overa32.finanzasclaras
// es la clase que es llamada por alertsviewmodel para poder obtener, añadir, actualizar o eliminar umbrales por ende es el que esta en medio con viewmodel y alerthresholdao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertThresholdsRepository(private val alertThresholdDao: AlertThresholdDao) {

    // Obtiene todos los umbrales y los mapea a la clase de UI
    fun getAllAlertThresholds(): Flow<List<AlertThreshold>> {
        return alertThresholdDao.getAllAlertThresholds().map { entities ->
            entities.map { it.toAlertThreshold() }
        }
    }

    suspend fun insertAlertThreshold(threshold: AlertThreshold) {
        alertThresholdDao.insertAlertThreshold(threshold.toAlertThresholdEntity())
    }

    suspend fun updateAlertThreshold(threshold: AlertThreshold) {
        alertThresholdDao.updateAlertThreshold(threshold.toAlertThresholdEntity())
    }

    suspend fun deleteAlertThreshold(thresholdId: Long) {
        alertThresholdDao.deleteAlertThreshold(thresholdId)
    }
}