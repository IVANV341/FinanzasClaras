
// Este archivo configura la base de datos Room para la aplicación.
// Define las entidades que se almacenarán (`AlertThresholdEntity` y `ChatHistoryEntry`),
// especifica la versión de la base de datos y los DAOs (`AlertThresholdDao`, `ChatHistoryDao`)
// para interactuar con las tablas. También incluye convertidores de tipos para manejar
// enumeraciones y fechas en la base de datos.

package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

// ¡IMPORTANTE! Aumenta la versión si ya tenías la 2 por cambios anteriores
// Añade ChatHistoryEntry a las entidades
@Database(entities = [AlertThresholdEntity::class, ChatHistoryEntry::class], version = 3, exportSchema = false) // ¡VERSIÓN AUMENTADA! ¡NUEVA ENTIDAD!
@TypeConverters(Converters::class, ChatConverters::class) // ¡AÑADIDO ChatConverters!
abstract class AppDatabase : RoomDatabase() {
    abstract fun alertThresholdDao(): AlertThresholdDao
    abstract fun chatHistoryDao(): ChatHistoryDao // ¡NUEVO!

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finanzas_claras_db"
                )
                    .fallbackToDestructiveMigration() // Asegúrate de que esto está para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Tus Converters existentes para AlertType (sin cambios)
class Converters {
    @TypeConverter
    fun fromAlertType(value: AlertType): String {
        return value.name
    }

    @TypeConverter
    fun toAlertType(value: String): AlertType {
        return enumValueOf(value)
    }
}

// ChatConverters ya está definido en ChatHistoryEntry.kt