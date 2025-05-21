package co.edu.unab.overa32.finanzasclaras

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ¡IMPORTANTE! Clase marcada como 'open' para permitir la herencia/mocking en previews
open class AlertThresholdsRepository(private val alertThresholdDao: AlertThresholdDao) {

    // ¡IMPORTANTE! Función marcada como 'open' para permitir el override en previews
    open fun getAllAlertThresholds(): Flow<List<AlertThreshold>> {
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