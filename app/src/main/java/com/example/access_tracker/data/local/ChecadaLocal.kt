package com.example.access_tracker.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Estados de una checada en la cola. Se guardan como texto para no necesitar convertidores. */
object EstadoEnvio {
    /** Todavia no llega al servidor: se va a reintentar. */
    const val PENDIENTE = "PENDIENTE"

    /** El servidor ya la tiene. No se vuelve a enviar; se borra sola despues de unos dias. */
    const val ENVIADA = "ENVIADA"

    /** El servidor la rechazo de forma definitiva (no se reintenta). Se le avisa a la persona una vez. */
    const val RECHAZADA = "RECHAZADA"
}

/**
 * Una checada (entrada o salida) hecha en este telefono que pasa por la cola de envio.
 * El [id] autogenerado conserva el orden de llegada: la entrada siempre se envia antes que la salida.
 */
@Entity(
    tableName = "cola_checadas",
    indices = [
        Index(value = ["uuid", "tipo"], unique = true),
        Index(value = ["usuarioId", "estado"]),
    ],
)
data class ChecadaLocal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** En las entradas es el uuid que la API usa para no duplicar. */
    val uuid: String,
    val tipo: String, // Pendiente.TIPO_ENTRADA | Pendiente.TIPO_SALIDA
    val usuarioId: Long,
    /** Hora real en que se toco el boton (ISO-8601 UTC). */
    val ocurridoEn: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    /** Solo si no hubo ubicacion: false = fuera de zona. Con coordenadas lo calcula la API. */
    val enZona: Boolean? = null,
    val estado: String = EstadoEnvio.PENDIENTE,
    /** Intentos de envio fallidos (sin red, error del servidor...). Solo informativo. */
    val intentos: Int = 0,
    /** Solo en RECHAZADA: por que no se pudo registrar. */
    val motivoRechazo: String? = null,
    /** RECHAZADA: ya se le mostro el aviso a la persona. */
    val avisoMostrado: Boolean = false,
    /** Cuando se guardo en el telefono (millis). */
    val creadoEn: Long = System.currentTimeMillis(),
    /** Cuando dejo de estar pendiente (enviada o rechazada), en millis. Sirve para la limpieza. */
    val procesadoEn: Long? = null,
)
