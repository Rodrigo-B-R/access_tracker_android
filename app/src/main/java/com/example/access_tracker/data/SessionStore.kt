package com.example.access_tracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.access_tracker.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Archivo de DataStore con la sesion: files/datastore/sesion.preferences_pb (excluido de los respaldos).
 * Se declara una sola vez: dos instancias del mismo archivo hacen que la app truene.
 * La primera vez copia lo que habia en SharedPreferences ("session.xml") y lo borra, asi quien ya
 * tenia la sesion iniciada no tiene que volver a entrar al actualizar la app.
 */
private val Context.sesionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sesion",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, "session")) },
)

/** Sesion guardada en el telefono. */
data class Session(
    val token: String,
    val role: String,
    val userId: Long,
    val name: String,
    val daycareId: Long?,
    val expiresAtMillis: Long,
) {
    fun vigente(ahora: Long = System.currentTimeMillis()) = ahora < expiresAtMillis
}

/** Claves dentro del archivo. Son los mismos nombres que usaba SharedPreferences: no cambiarlos. */
private object ClavesSesion {
    val TOKEN = stringPreferencesKey("token")
    val ROLE = stringPreferencesKey("role")
    val USER_ID = longPreferencesKey("userId")
    val NAME = stringPreferencesKey("name")
    val DAYCARE_ID = longPreferencesKey("daycareId") // -1 = sin estancia
    val EXPIRES_AT = longPreferencesKey("expiresAt")
}

class SessionStore(private val store: DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.sesionDataStore)

    /** La sesion actual; emite de nuevo cada vez que se guarda o se borra. null = no hay sesion. */
    val sesion: Flow<Session?> = store.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e } // archivo ilegible: sin sesion
        .map { p ->
            val token = p[ClavesSesion.TOKEN] ?: return@map null
            val role = p[ClavesSesion.ROLE] ?: return@map null
            Session(
                token = token,
                role = role,
                userId = p[ClavesSesion.USER_ID] ?: 0,
                name = p[ClavesSesion.NAME] ?: "",
                daycareId = (p[ClavesSesion.DAYCARE_ID] ?: -1L).takeIf { it >= 0 },
                expiresAtMillis = p[ClavesSesion.EXPIRES_AT] ?: 0,
            )
        }

    /** Lee la sesion una vez (sin quedarse escuchando). */
    suspend fun leer(): Session? = sesion.first()

    /** Guarda todo junto: o se guarda completo, o nada. */
    suspend fun guardar(r: LoginResponse) {
        store.edit { p ->
            p[ClavesSesion.TOKEN] = r.token
            p[ClavesSesion.ROLE] = r.role
            p[ClavesSesion.USER_ID] = r.user.id
            p[ClavesSesion.NAME] = r.user.name
            p[ClavesSesion.DAYCARE_ID] = r.user.daycareId ?: -1L
            p[ClavesSesion.EXPIRES_AT] = System.currentTimeMillis() + r.expiresIn * 1000
        }
    }

    suspend fun borrar() {
        store.edit { it.clear() }
    }
}
