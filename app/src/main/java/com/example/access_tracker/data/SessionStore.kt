package com.example.access_tracker.data

import android.content.Context
import com.example.access_tracker.model.LoginResponse

/** Sesion guardada en el telefono. El archivo "session.xml" esta excluido de los respaldos. */
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

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun guardar(r: LoginResponse) {
        prefs.edit()
            .putString("token", r.token)
            .putString("role", r.role)
            .putLong("userId", r.user.id)
            .putString("name", r.user.name)
            .putLong("daycareId", r.user.daycareId ?: -1L)
            .putLong("expiresAt", System.currentTimeMillis() + r.expiresIn * 1000)
            .apply()
    }

    fun leer(): Session? {
        val token = prefs.getString("token", null) ?: return null
        val role = prefs.getString("role", null) ?: return null
        val daycare = prefs.getLong("daycareId", -1L)
        return Session(
            token = token,
            role = role,
            userId = prefs.getLong("userId", 0),
            name = prefs.getString("name", "") ?: "",
            daycareId = daycare.takeIf { it >= 0 },
            expiresAtMillis = prefs.getLong("expiresAt", 0),
        )
    }

    fun borrar() = prefs.edit().clear().apply()
}
