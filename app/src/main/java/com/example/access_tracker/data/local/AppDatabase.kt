package com.example.access_tracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local. Solo guarda lo que tiene que funcionar sin internet: la cola de checadas.
 *
 * Versiones:
 * - 1: usuarioId numerico.
 * - 2: usuarioId es UUID (texto), igual que en la API. La cola de la version 1 se borra: sus ids
 *   numericos ya no existen en el servidor y no hay forma de convertirlos.
 */
@Database(entities = [ChecadaLocal::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checadaDao(): ChecadaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "access_tracker.db",
                )
                    // Solo desde la version 1 se borra y se crea de nuevo. Cambios futuros: escribir una Migration.
                    .fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)
                    .build().also { INSTANCE = it }
            }
    }
}
