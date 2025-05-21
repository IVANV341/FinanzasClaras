package co.edu.unab.overa32.finanzasclaras
// da funciones para toods los umbrales y la opcion de poder añadir uno nuevo, cambiar su estado a activo o no, cambiar su monto y eliminarlo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertThresholdDao {
    @Query("SELECT * FROM alert_thresholds ORDER BY amount ASC")
    fun getAllAlertThresholds(): Flow<List<AlertThresholdEntity>>

    @Insert
    suspend fun insertAlertThreshold(threshold: AlertThresholdEntity): Long

    @Update
    suspend fun updateAlertThreshold(threshold: AlertThresholdEntity)

    @Query("DELETE FROM alert_thresholds WHERE id = :thresholdId")
    suspend fun deleteAlertThreshold(thresholdId: Long)
}