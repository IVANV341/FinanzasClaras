package co.edu.unab.overa32.finanzasclaras

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [AlertThresholdEntity::class], version = 2, exportSchema = false) // ¡VERSIÓN AUMENTADA A 2!
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alertThresholdDao(): AlertThresholdDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finanzas_claras_db" // Nombre de tu base de datos
                )
                    .fallbackToDestructiveMigration() // ¡NUEVO! Destruye y recrea la DB si el esquema cambia
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Tus Converters para AlertType (sin cambios)
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