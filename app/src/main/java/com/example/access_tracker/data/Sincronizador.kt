package com.example.access_tracker.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.access_tracker.model.Checada
import com.example.access_tracker.model.EntradaRequest
import com.example.access_tracker.model.RespuestaChecada
import com.example.access_tracker.model.SalidaRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

/** Que hacer con una checada pendiente segun lo que contesto la API. */
sealed interface Decision {
    /** Quedo registrada (o la API ya tenia ese resultado): se quita de la cola. */
    data object Enviada : Decision

    /** La API la rechazo de forma definitiva: se quita de la cola y se le avisa a la persona. */
    data class Descartar(val motivo: String) : Decision

    /** No se pudo (sin red, limite, error del servidor, sesion): se conserva y se reintenta despues. */
    data object Reintentar : Decision
}

/**
 * Clasifica la respuesta de la API para una checada pendiente.
 *
 * - 409 cuenta como enviada: "entrada ya abierta" o "no hay entrada abierta" significan que el estado
 *   que la persona queria ya existe (por ejemplo, la respuesta se perdio despues de que el servidor
 *   guardo la salida y el reintento llega de nuevo).
 * - Los demas 4xx (turno inexistente, hora invalida) no van a cambiar si se reintenta: se descartan.
 */
fun decidir(tipo: String, r: ApiResult<*>): Decision = when (r) {
    is ApiResult.Ok -> Decision.Enviada
    is ApiResult.Error -> when (val e = r.error) {
        ApiError.SinConexion, ApiError.NoAutorizado, is ApiError.Limitado -> Decision.Reintentar
        is ApiError.Servidor -> when {
            e.codigo == 409 -> Decision.Enviada
            e.codigo in 400..499 -> Decision.Descartar(motivoRechazo(tipo))
            else -> Decision.Reintentar
        }
    }
}

fun motivoRechazo(tipo: String): String =
    if (tipo == Pendiente.TIPO_ENTRADA) "no tienes un turno asignado ese día o la hora no es válida"
    else "la hora de salida no es válida"

/** Resultado de un intento de enviar la cola. */
data class ResultadoSync(
    /** Quedan checadas por enviar. */
    val quedanPendientes: Boolean,
    /** El intento fallo por falta de conexion (no vale la pena llamar a otros endpoints ahora). */
    val sinConexion: Boolean,
)

/** Envia a la API las checadas guardadas sin internet, en orden y una a la vez. */
class Sincronizador(
    private val api: ApiClient,
    private val cola: ColaChecadas,
    private val sesion: SessionStore,
    /** Pide al sistema un reintento automatico cuando vuelva el internet. */
    private val programarReintento: () -> Unit,
) {
    // Un solo envio a la vez entre la pantalla y el worker: una salida repetida no es idempotente.
    private val candado = Mutex()

    /** Pide al sistema que reintente el envio cuando haya internet. */
    fun programar() = programarReintento()

    suspend fun sincronizar(): ResultadoSync = candado.withLock {
        val usuario = sesion.leer()?.userId ?: return ResultadoSync(false, false)
        for (p in cola.pendientes(usuario)) {
            val respuesta = enviar(p)
            when (val d = decidir(p.tipo, respuesta)) {
                Decision.Enviada -> cola.quitar(p.uuid, p.tipo)
                is Decision.Descartar -> {
                    cola.quitar(p.uuid, p.tipo)
                    cola.agregarRechazo(Rechazo(p.tipo, p.ocurridoEn, d.motivo, usuario))
                }
                Decision.Reintentar -> {
                    programarReintento()
                    // Si fue falta de red, lo que sigue tampoco saldra: se corta aqui (y se conserva el orden).
                    val sinRed = respuesta is ApiResult.Error && respuesta.error == ApiError.SinConexion
                    return ResultadoSync(quedanPendientes = true, sinConexion = sinRed)
                }
            }
        }
        ResultadoSync(quedanPendientes = false, sinConexion = false)
    }

    private suspend fun enviar(p: Pendiente): ApiResult<*> {
        return if (p.tipo == Pendiente.TIPO_ENTRADA) {
            api.post(
                "/time-records/clock-in",
                EntradaRequest(p.uuid, p.latitud, p.longitud, p.enZona, occurredAt = p.ocurridoEn),
                EntradaRequest.serializer(),
                RespuestaChecada.serializer(),
            )
        } else {
            api.post(
                "/time-records/clock-out",
                SalidaRequest(occurredAt = p.ocurridoEn),
                SalidaRequest.serializer(),
                Checada.serializer(),
            )
        }
    }
}

/** Reintenta el envio de la cola cuando hay internet, aunque la app este cerrada. */
class SincronizarChecadasWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val r = ServiceLocator.sincronizador.sincronizar()
        return if (r.quedanPendientes) Result.retry() else Result.success()
    }

    companion object {
        private const val NOMBRE = "sincronizar_checadas"

        fun programar(context: Context) {
            val peticion = OneTimeWorkRequestBuilder<SincronizarChecadasWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(NOMBRE, ExistingWorkPolicy.APPEND_OR_REPLACE, peticion)
        }
    }
}
