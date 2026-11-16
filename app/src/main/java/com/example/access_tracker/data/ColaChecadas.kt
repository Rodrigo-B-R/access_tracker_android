package com.example.access_tracker.data

import android.content.Context
import com.example.access_tracker.data.local.ChecadaDao
import com.example.access_tracker.data.local.ChecadaLocal
import com.example.access_tracker.data.local.EstadoEnvio
import com.example.access_tracker.model.EstadoChecada
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * Almacen de texto clave-valor (en la app, SharedPreferences; en los tests, un mapa en memoria).
 * Lo usa CacheEstado; la cola de checadas ya vive en Room.
 */
interface Almacen {
    fun leer(clave: String): String?
    fun escribir(clave: String, valor: String?)
}

class PrefsAlmacen(context: Context, nombre: String) : Almacen {
    private val prefs = context.getSharedPreferences(nombre, Context.MODE_PRIVATE)
    override fun leer(clave: String): String? = prefs.getString(clave, null)
    override fun escribir(clave: String, valor: String?) {
        prefs.edit().apply { if (valor == null) remove(clave) else putString(clave, valor) }.apply()
    }
}

/** Una checada hecha sin internet, esperando enviarse. */
@Serializable
data class Pendiente(
    /** Identificador de la checada. En las entradas es el uuid que la API usa para no duplicar. */
    val uuid: String,
    val tipo: String, // TIPO_ENTRADA | TIPO_SALIDA
    val usuarioId: Long,
    /** Hora real en que se toco el boton (ISO-8601 UTC). */
    val ocurridoEn: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    /** Solo si no hubo ubicacion: false = fuera de zona. Con coordenadas lo calcula la API. */
    val enZona: Boolean? = null,
) {
    companion object {
        const val TIPO_ENTRADA = "entrada"
        const val TIPO_SALIDA = "salida"
    }
}

/** Una checada que la API rechazo de forma definitiva (no se reintenta), lista para avisar. */
@Serializable
data class Rechazo(val tipo: String, val ocurridoEn: String, val motivo: String, val usuarioId: Long)

/**
 * Cola persistente de checadas, guardada en Room (tabla cola_checadas). Cada checada pasa por
 * PENDIENTE -> ENVIADA o RECHAZADA; solo las PENDIENTE se envian, asi nunca se manda dos veces.
 * El orden importa (entrada antes que salida): se respeta el orden de llegada. Cada fila lleva el
 * usuario que la hizo: nunca se envia con la sesion de otra persona.
 *
 * Las ENVIADA (y las RECHAZADA cuyo aviso ya se vio) se borran solas despues de [DIAS_RETENCION] dias:
 * el historial real vive en el servidor y cada checada lleva la ubicacion de la persona.
 */
class ColaChecadas(
    private val dao: ChecadaDao,
    /** Donde guardaba la cola la version anterior (SharedPreferences). Se pasa una vez a Room y se vacia. */
    private val almacenViejo: Almacen? = null,
    private val reloj: () -> Long = System::currentTimeMillis,
) {
    private val candadoMigracion = Mutex()

    @Volatile
    private var migrada = almacenViejo == null

    suspend fun pendientes(usuarioId: Long): List<Pendiente> {
        migrar()
        return dao.pendientes(usuarioId).map { it.aPendiente() }
    }

    suspend fun hayPendientes(): Boolean {
        migrar()
        return dao.hayPendientes()
    }

    /** Guarda una checada como PENDIENTE. Si ya existia el mismo (uuid, tipo) no se duplica. */
    suspend fun agregar(p: Pendiente) {
        migrar()
        dao.insertar(p.aLocal())
    }

    suspend fun marcarEnviada(uuid: String, tipo: String) {
        dao.marcarEnviada(uuid, tipo, reloj())
    }

    suspend fun marcarRechazada(uuid: String, tipo: String, motivo: String) {
        dao.marcarRechazada(uuid, tipo, motivo, reloj())
    }

    suspend fun sumarIntento(uuid: String, tipo: String) {
        dao.sumarIntento(uuid, tipo)
    }

    /** Rechazos que todavia no se le muestran a [usuarioId]. */
    suspend fun rechazos(usuarioId: Long): List<Rechazo> {
        migrar()
        return dao.rechazosSinAviso(usuarioId).map {
            Rechazo(it.tipo, it.ocurridoEn, it.motivoRechazo.orEmpty(), it.usuarioId)
        }
    }

    /** Marca los rechazos de [usuarioId] como ya mostrados (no se vuelven a avisar). */
    suspend fun limpiarRechazos(usuarioId: Long) {
        dao.marcarAvisosMostrados(usuarioId)
    }

    /** Borra las checadas ya terminadas con mas de [DIAS_RETENCION] dias. Las PENDIENTE no se tocan. */
    suspend fun limpiarViejas(): Int =
        dao.borrarTerminadasAntesDe(reloj() - TimeUnit.DAYS.toMillis(DIAS_RETENCION))

    /**
     * Pasa a Room lo que la version anterior dejo en SharedPreferences, una sola vez. Primero se inserta
     * todo y despues se vacia el almacen viejo: si la app se cierra a la mitad, al reintentar el indice
     * unico (uuid, tipo) evita duplicados.
     */
    private suspend fun migrar() {
        if (migrada) return
        candadoMigracion.withLock {
            if (migrada) return
            val viejo = almacenViejo ?: return
            for (p in leerViejo(viejo, CLAVE_PENDIENTES, ListSerializer(Pendiente.serializer()))) {
                dao.insertar(p.aLocal())
            }
            for (r in leerViejo(viejo, CLAVE_RECHAZOS, ListSerializer(Rechazo.serializer()))) {
                dao.insertar(
                    ChecadaLocal(
                        uuid = "rechazo-${r.tipo}-${r.ocurridoEn}", tipo = r.tipo, usuarioId = r.usuarioId,
                        ocurridoEn = r.ocurridoEn, estado = EstadoEnvio.RECHAZADA, motivoRechazo = r.motivo,
                        procesadoEn = reloj(),
                    )
                )
            }
            viejo.escribir(CLAVE_PENDIENTES, null)
            viejo.escribir(CLAVE_RECHAZOS, null)
            migrada = true
        }
    }

    private fun <T> leerViejo(almacen: Almacen, clave: String, serializer: KSerializer<List<T>>): List<T> {
        val texto = almacen.leer(clave) ?: return emptyList()
        return try {
            json.decodeFromString(serializer, texto)
        } catch (e: Exception) {
            emptyList() // dato corrupto: se ignora en vez de tumbar la app
        }
    }

    private fun Pendiente.aLocal() = ChecadaLocal(
        uuid = uuid, tipo = tipo, usuarioId = usuarioId, ocurridoEn = ocurridoEn,
        latitud = latitud, longitud = longitud, enZona = enZona, creadoEn = reloj(),
    )

    private fun ChecadaLocal.aPendiente() = Pendiente(
        uuid = uuid, tipo = tipo, usuarioId = usuarioId, ocurridoEn = ocurridoEn,
        latitud = latitud, longitud = longitud, enZona = enZona,
    )

    companion object {
        /** Dias que se conservan las checadas ya enviadas antes de borrarlas del telefono. */
        const val DIAS_RETENCION = 7L

        private val json = Json { ignoreUnknownKeys = true }
        private const val CLAVE_PENDIENTES = "pendientes"
        private const val CLAVE_RECHAZOS = "rechazos"
    }
}

/** Ultimo estado de /time-records/status, para poder mostrar el turno y checar aunque no haya internet. */
@Serializable
data class EstadoGuardado(val usuarioId: Long, val fecha: String, val estado: EstadoChecada)

class CacheEstado(private val almacen: Almacen) {
    private val json = Json { ignoreUnknownKeys = true }

    fun guardar(usuarioId: Long, fecha: String, estado: EstadoChecada) {
        almacen.escribir(CLAVE, json.encodeToString(EstadoGuardado.serializer(), EstadoGuardado(usuarioId, fecha, estado)))
    }

    /** Solo devuelve lo guardado por [usuarioId] (nunca el estado de otra persona). */
    fun leer(usuarioId: Long): EstadoGuardado? {
        val texto = almacen.leer(CLAVE) ?: return null
        return try {
            json.decodeFromString(EstadoGuardado.serializer(), texto).takeIf { it.usuarioId == usuarioId }
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val CLAVE = "estado"
    }
}
