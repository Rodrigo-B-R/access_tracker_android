package com.example.access_tracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.access_tracker.model.EstadoChecada
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Archivo de DataStore con la ultima copia del estado: files/datastore/cache_estado.preferences_pb.
 * La primera vez copia lo que habia en SharedPreferences ("cache_estado.xml") y lo borra.
 */
private val Context.cacheEstadoDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "cache_estado",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, "cache_estado")) },
)

/** Ultimo estado de /time-records/status, para poder mostrar el turno y checar aunque no haya internet. */
@Serializable
data class EstadoGuardado(val usuarioId: Long, val fecha: String, val estado: EstadoChecada)

class CacheEstado(private val store: DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.cacheEstadoDataStore)

    suspend fun guardar(usuarioId: Long, fecha: String, estado: EstadoChecada) {
        val texto = json.encodeToString(EstadoGuardado.serializer(), EstadoGuardado(usuarioId, fecha, estado))
        store.edit { it[CLAVE] = texto }
    }

    /** Solo devuelve lo guardado por [usuarioId] (nunca el estado de otra persona). */
    suspend fun leer(usuarioId: Long): EstadoGuardado? {
        val p = store.data.catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }.first()
        val texto = p[CLAVE] ?: return null
        return try {
            json.decodeFromString(EstadoGuardado.serializer(), texto).takeIf { it.usuarioId == usuarioId }
        } catch (e: Exception) {
            null // dato corrupto: se ignora en vez de tumbar la app
        }
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }

        /** Mismo nombre que en SharedPreferences, para que la migracion lo encuentre. */
        val CLAVE = stringPreferencesKey("estado")
    }
}
