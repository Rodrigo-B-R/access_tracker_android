package com.example.access_tracker.data

import android.content.Context
import com.example.access_tracker.model.EstadoChecada
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Almacen de texto clave-valor (en la app, SharedPreferences; en los tests, un mapa en memoria). */
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

/** Una checada pendiente que la API rechazo de forma definitiva (no se reintenta). */
@Serializable
data class Rechazo(val tipo: String, val ocurridoEn: String, val motivo: String, val usuarioId: Long)

/**
 * Cola persistente de checadas hechas sin internet. El orden importa (entrada antes que salida), por eso
 * es una lista en orden de llegada. Cada elemento lleva el usuario que lo hizo: nunca se envia con la
 * sesion de otra persona.
 */
class ColaChecadas(private val almacen: Almacen) {
    private val json = Json { ignoreUnknownKeys = true }
    private val listaPendientes = ListSerializer(Pendiente.serializer())
    private val listaRechazos = ListSerializer(Rechazo.serializer())

    @Synchronized
    private fun todos(): List<Pendiente> = leer(CLAVE_PENDIENTES, listaPendientes)

    @Synchronized
    fun pendientes(usuarioId: Long): List<Pendiente> = todos().filter { it.usuarioId == usuarioId }

    @Synchronized
    fun hayPendientes(): Boolean = todos().isNotEmpty()

    @Synchronized
    fun agregar(p: Pendiente) {
        almacen.escribir(CLAVE_PENDIENTES, json.encodeToString(listaPendientes, todos() + p))
    }

    @Synchronized
    fun quitar(uuid: String, tipo: String) {
        // La clave es (uuid, tipo) por si alguna vez coinciden.
        val resto = todos().filterNot { it.uuid == uuid && it.tipo == tipo }
        almacen.escribir(CLAVE_PENDIENTES, json.encodeToString(listaPendientes, resto))
    }

    @Synchronized
    fun agregarRechazo(r: Rechazo) {
        almacen.escribir(CLAVE_RECHAZOS, json.encodeToString(listaRechazos, leer(CLAVE_RECHAZOS, listaRechazos) + r))
    }

    @Synchronized
    fun rechazos(usuarioId: Long): List<Rechazo> =
        leer(CLAVE_RECHAZOS, listaRechazos).filter { it.usuarioId == usuarioId }

    @Synchronized
    fun limpiarRechazos(usuarioId: Long) {
        val resto = leer(CLAVE_RECHAZOS, listaRechazos).filterNot { it.usuarioId == usuarioId }
        almacen.escribir(CLAVE_RECHAZOS, json.encodeToString(listaRechazos, resto))
    }

    private fun <T> leer(clave: String, serializer: kotlinx.serialization.KSerializer<List<T>>): List<T> {
        val texto = almacen.leer(clave) ?: return emptyList()
        return try {
            json.decodeFromString(serializer, texto)
        } catch (e: Exception) {
            emptyList() // dato corrupto: se ignora en vez de tumbar la app
        }
    }

    private companion object {
        const val CLAVE_PENDIENTES = "pendientes"
        const val CLAVE_RECHAZOS = "rechazos"
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
