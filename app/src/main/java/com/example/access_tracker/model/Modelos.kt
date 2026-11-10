package com.example.access_tracker.model

import kotlinx.serialization.Serializable

// DTOs de la API (camelCase, igual que app/schemas.py de sonrisas_cristal_api).

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class UsuarioResumen(
    val id: Long,
    val name: String,
    val daycareId: Long? = null,
)

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,          // segundos
    val role: String,             // "employee" | "manager" | "director"
    val user: UsuarioResumen,
)

// ── Catalogos y horario ─────────────────────────────────────────────

@Serializable
data class Turno(val id: Long, val name: String, val startTime: String, val endTime: String)

@Serializable
data class Sala(val id: Long, val name: String, val minStaff: Int = 0, val daycareId: Long = 0)

@Serializable
data class Ubicacion(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
)

@Serializable
data class DiaHorario(
    val id: Long,
    val weekday: String,          // "monday".."sunday"
    val shiftId: Long,
    val roomId: Long,
    val shift: Turno,
    val room: Sala,
)

@Serializable
data class Horario(
    val id: Long,
    val userId: Long,
    val validFrom: String,
    val validUntil: String,
    val days: List<DiaHorario>,
)

// ── Checadas ────────────────────────────────────────────────────────

@Serializable
data class Checada(
    val id: Long,
    val uuid: String,
    val clockInAt: String,        // ISO-8601 UTC
    val clockOutAt: String? = null,
    val withinZone: Boolean,
    val room: Sala,
    val shift: Turno,
)

@Serializable
data class EstadoChecada(
    val inside: Boolean,
    val openRecord: Checada? = null,
    val todayShifts: List<DiaHorario> = emptyList(),
    val location: Ubicacion,
)

@Serializable
data class EntradaRequest(
    val uuid: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val withinZone: Boolean? = null,
)

@Serializable
class SalidaRequest

@Serializable
data class RespuestaChecada(val record: Checada, val repeated: Boolean = false)

// ── Asistencia ──────────────────────────────────────────────────────

@Serializable
data class DiaAsistenciaDto(
    val date: String,
    val clockIn: String? = null,   // "HH:MM"
    val clockOut: String? = null,
    val status: String,            // present, late, absent, incident, no_record, pending, day_off, ...
    val reason: String? = null,
    val lateMinutes: Int = 0,
)

@Serializable
data class ResumenAsistencia(
    val workedDays: Int = 0,
    val lates: Int = 0,
    val absences: Int = 0,
    val incidents: Int = 0,
)

@Serializable
data class MiAsistencia(
    val startDate: String,
    val endDate: String,
    val summary: ResumenAsistencia,
    val days: List<DiaAsistenciaDto>,
)
