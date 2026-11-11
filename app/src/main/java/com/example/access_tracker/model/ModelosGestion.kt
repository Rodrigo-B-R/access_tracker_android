package com.example.access_tracker.model

import kotlinx.serialization.Serializable

// DTOs de gestion (responsable y direccion). Nombres exactos de app/schemas.py de la API.

@Serializable
data class Referencia(val id: Long, val name: String)

@Serializable
data class Puesto(val id: Long, val name: String)

@Serializable
data class MotivoAusencia(val id: Long, val description: String)

@Serializable
data class Catalogos(
    val positions: List<Puesto> = emptyList(),
    val shifts: List<Turno> = emptyList(),
    val terminationReasons: List<Puesto> = emptyList(),
    val absenceReasons: List<MotivoAusencia> = emptyList(),
)

@Serializable
data class Rol(val id: Long = 0, val name: String)

@Serializable
data class EstanciaBasica(val id: Long, val name: String)

@Serializable
data class Estancia(
    val id: Long,
    val name: String,
    val location: Ubicacion? = null,
    val rooms: List<Sala> = emptyList(),
)

// ── Personal ────────────────────────────────────────────────────────

@Serializable
data class Persona(
    val id: Long,
    val name: String,
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val isActive: Boolean = true,
    val role: Rol? = null,
    val position: Puesto? = null,
    val daycare: EstanciaBasica? = null,
) {
    val iniciales: String
        get() = name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
}

@Serializable
data class Movimiento(
    val id: Long,
    val type: String,             // hire | termination | transfer
    val date: String,
    val notes: String? = null,
    val terminationReason: Puesto? = null,
)

@Serializable
data class PersonaDetalle(
    val id: Long,
    val name: String,
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val isActive: Boolean = true,
    val role: Rol? = null,
    val position: Puesto? = null,
    val daycare: EstanciaBasica? = null,
    val movements: List<Movimiento> = emptyList(),
    val currentSchedule: Horario? = null,
)

@Serializable
data class AltaPersonaRequest(
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val positionId: Long,
    val password: String? = null,
)

@Serializable
data class EdicionPersonaRequest(
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val positionId: Long? = null,
)

@Serializable
data class PersonaCreada(val id: Long, val name: String, val temporaryPassword: String)

@Serializable
data class BajaRequest(val terminationReasonId: Long, val notes: String? = null)

@Serializable
data class TrasladoRequest(val daycareId: Long, val notes: String? = null)

@Serializable
data class PasswordTemporal(val temporaryPassword: String)

// ── Horarios ────────────────────────────────────────────────────────

@Serializable
data class PersonaResumen(val id: Long, val name: String, val position: Puesto? = null)

@Serializable
data class HorarioDePersona(val person: PersonaResumen, val schedule: Horario? = null)

@Serializable
data class DiaHorarioRequest(val day: String, val shiftId: Long, val roomId: Long)

@Serializable
data class HorarioRequest(val validFrom: String? = null, val days: List<DiaHorarioRequest>)

// ── Checadas de gestion ─────────────────────────────────────────────

@Serializable
data class LoteRequest(
    val userIds: List<Long>,
    /** Hora local sin zona ("2026-11-11T07:00:00"); la API la lee en America/Monterrey. */
    val occurredAt: String? = null,
    val roomId: Long? = null,
    val shiftId: Long? = null,
)

@Serializable
data class OmitidaLote(val userId: Long, val reason: String)

@Serializable
data class RespuestaLote(
    val recorded: List<Checada> = emptyList(),
    val skipped: List<OmitidaLote> = emptyList(),
)

// ── Asistencia (gestion) ────────────────────────────────────────────

@Serializable
data class TurnoDelDia(
    val shiftId: Long,
    val shift: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val room: Referencia? = null,
)

@Serializable
data class DiaAsistenciaGestion(
    val date: String,
    val shifts: List<TurnoDelDia> = emptyList(),
    val clockIn: String? = null,
    val clockOut: String? = null,
    val status: String,
    val reason: String? = null,
    val attendanceId: Long? = null,
    val lateMinutes: Int = 0,
)

@Serializable
data class AsistenciaPersona(
    val person: Persona,
    val summary: ResumenAsistencia = ResumenAsistencia(),
    val days: List<DiaAsistenciaGestion> = emptyList(),
)

@Serializable
data class AsistenciaRequest(
    val userId: Long,
    val date: String,
    val status: String,           // present | late | absent | incident
    val absenceReasonId: Long? = null,
)

@Serializable
data class AsistenciaRegistrada(val id: Long, val status: String)

// ── Avisos (gestion) ────────────────────────────────────────────────

@Serializable
data class AvisoGestion(
    val id: Long,
    val subject: String,
    val noticeType: String,
    val message: String,
    val requiresConfirmation: Boolean = false,
    val author: AutorAviso,
    val recipientCount: Int = 0,
    val confirmedCount: Int = 0,
)

@Serializable
data class Lector(val id: Long, val name: String, val readAt: String? = null)

@Serializable
data class AvisoDetalleGestion(
    val id: Long,
    val subject: String,
    val noticeType: String,
    val message: String,
    val requiresConfirmation: Boolean = false,
    val author: AutorAviso,
    val recipientCount: Int = 0,
    val confirmed: List<Lector> = emptyList(),
    val pending: List<Referencia> = emptyList(),
)

@Serializable
data class AvisoNuevo(
    val subject: String,
    val message: String,
    val noticeType: String,
    val target: String = "all",
    val shiftId: Long? = null,
    val roomId: Long? = null,
    val userIds: List<Long>? = null,
    val requiresConfirmation: Boolean = false,
)

// ── Salas ───────────────────────────────────────────────────────────

@Serializable
data class SalaRequest(val name: String? = null, val minStaff: Int? = null)

// ── Reportes ────────────────────────────────────────────────────────

@Serializable
data class CoberturaTurno(
    val shiftId: Long,
    val shift: String = "",
    val scheduled: Int = 0,
    val present: Int = 0,
    val started: Boolean = false,
    val uncovered: Boolean = false,
    val missing: Int = 0,
    val belowMinimum: Boolean = false,
)

@Serializable
data class CoberturaSala(
    val room: Referencia,
    val minimum: Int = 0,
    val shifts: List<CoberturaTurno> = emptyList(),
)

@Serializable
data class Cobertura(
    val daycare: Referencia,
    val date: String,
    val present: Int = 0,
    val scheduled: Int = 0,
    val pending: Int = 0,
    val complete: Boolean = true,
    val alerts: List<String> = emptyList(),
    val rooms: List<CoberturaSala> = emptyList(),
)

@Serializable
data class ResumenEstancia(
    val id: Long,
    val name: String,
    val complete: Boolean,
    val alerts: List<String> = emptyList(),
    val present: Int = 0,
    val scheduled: Int = 0,
)

@Serializable
data class ResumenDireccion(
    val date: String,
    val completeCount: Int = 0,
    val understaffedCount: Int = 0,
    val daycares: List<ResumenEstancia> = emptyList(),
)

@Serializable
data class EstadisticaEstancia(
    val daycare: Referencia,
    val hires: Int = 0,
    val terminations: Int = 0,
    val transfersIn: Int = 0,
    val transfersOut: Int = 0,
    val understaffedDays: Int = 0,
    val absences: Int = 0,
    val lates: Int = 0,
)

@Serializable
data class Estadisticas(
    val startDate: String,
    val endDate: String,
    val hires: Int = 0,
    val terminations: Int = 0,
    val absences: Int = 0,
    val lates: Int = 0,
    val understaffedDays: Int = 0,
    val absenteeism: Double = 0.0,
    val byDaycare: List<EstadisticaEstancia> = emptyList(),
)

@Serializable
data class EventoAuditoria(
    val id: Long,
    val action: String,
    val table: String,
    val recordId: Long = 0,
    val createdAt: String,
    val user: Referencia? = null,
)

@Serializable
data class MovimientoDetalle(
    val id: Long,
    val type: String,             // hire | termination | transfer
    val date: String,
    val notes: String? = null,
    val user: Referencia,
    val doneBy: Referencia,
    val originDaycare: Referencia? = null,
    val destinationDaycare: Referencia? = null,
    val terminationReason: Puesto? = null,
)
