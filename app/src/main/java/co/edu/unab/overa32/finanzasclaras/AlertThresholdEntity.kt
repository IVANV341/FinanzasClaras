package co.edu.unab.overa32.finanzasclaras

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta es la entidad de base de datos para Room
@Entity(tableName = "alert_thresholds")
data class AlertThresholdEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean,
    @ColumnInfo(name = "alert_type") val type: AlertType // ¡IMPORTANTE! Esta propiedad es la nueva para el tipo de alerta
)

// Funciones de mapeo para convertir entre tu data class de UI y la entidad de Room
fun AlertThresholdEntity.toAlertThreshold(): AlertThreshold {
    return AlertThreshold(id, amount, isEnabled, type) // Aquí se usa 'type' para construir AlertThreshold
}

fun AlertThreshold.toAlertThresholdEntity(): AlertThresholdEntity {
    return AlertThresholdEntity(id, amount, isEnabled, type) // Aquí se usa 'type' para construir AlertThresholdEntity
}