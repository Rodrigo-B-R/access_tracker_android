package com.example.access_tracker.screens.empleada

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.access_tracker.data.ApiError
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Gps
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.model.Aviso
import com.example.access_tracker.model.Checada
import com.example.access_tracker.model.EntradaRequest
import com.example.access_tracker.model.EstadoChecada
import com.example.access_tracker.model.RespuestaChecada
import com.example.access_tracker.model.SalidaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import java.util.UUID

data class ChecarEstado(
    val cargando: Boolean = true,
    val datos: EstadoChecada? = null,
    val enviando: Boolean = false,
    /** Resultado de la ultima accion o error recuperable (se muestra bajo el boton). */
    val mensaje: String? = null,
    /** Error al cargar el estado (pantalla de reintento). */
    val errorCarga: String? = null,
    /** Avisos sin leer, para el globito de la barra superior. */
    val avisosSinLeer: Int = 0,
)

class ChecarViewModel(app: Application) : AndroidViewModel(app) {
    private val api = ServiceLocator.api
    private val _estado = MutableStateFlow(ChecarEstado())
    val estado: StateFlow<ChecarEstado> = _estado.asStateFlow()

    /**
     * uuid de la entrada en curso. Se reutiliza si el intento falla por red, para que un reintento
     * nunca duplique la checada (la API responde repeated=true si ya la tenia).
     */
    private var uuidPendiente: String? = null

    fun cargar() {
        _estado.update { it.copy(cargando = it.datos == null, errorCarga = null) }
        viewModelScope.launch {
            when (val r = api.get("/time-records/status", EstadoChecada.serializer())) {
                is ApiResult.Ok -> _estado.update { it.copy(cargando = false, datos = r.value) }
                is ApiResult.Error -> _estado.update {
                    it.copy(cargando = false, errorCarga = textoError(r.error))
                }
            }
            // El contador es secundario: si falla, se deja el que haya.
            val avisos = api.get("/notices/inbox", ListSerializer(Aviso.serializer()))
            if (avisos is ApiResult.Ok) {
                _estado.update { it.copy(avisosSinLeer = avisos.value.count { a -> !a.read }) }
            }
        }
    }

    fun registrarEntrada() {
        if (_estado.value.enviando) return
        _estado.update { it.copy(enviando = true, mensaje = null) }
        viewModelScope.launch {
            val uuid = uuidPendiente ?: UUID.randomUUID().toString().also { uuidPendiente = it }
            val ubicacion = Gps.actual(getApplication())
            // Sin ubicacion NO se manda withinZone=true: la API lo daria por valido. Se marca fuera de zona.
            val cuerpo = if (ubicacion != null) {
                EntradaRequest(uuid, ubicacion.latitude, ubicacion.longitude)
            } else {
                EntradaRequest(uuid, withinZone = false)
            }
            when (val r = api.post(
                "/time-records/clock-in", cuerpo, EntradaRequest.serializer(), RespuestaChecada.serializer()
            )) {
                is ApiResult.Ok -> {
                    uuidPendiente = null
                    _estado.update {
                        it.copy(
                            enviando = false,
                            mensaje = if (ubicacion == null) {
                                "No se pudo leer tu ubicación: la entrada quedó como fuera de zona."
                            } else null,
                        )
                    }
                    cargar()
                }
                is ApiResult.Error -> {
                    val e = r.error
                    // 400/409 los responde la API (sin turno, entrada ya abierta, uuid repetido): no se reintenta.
                    if (e is ApiError.Servidor) uuidPendiente = null
                    _estado.update { it.copy(enviando = false, mensaje = textoEntrada(e)) }
                    if (e is ApiError.Servidor && e.codigo == 409) cargar()
                }
            }
        }
    }

    fun registrarSalida() {
        if (_estado.value.enviando) return
        _estado.update { it.copy(enviando = true, mensaje = null) }
        viewModelScope.launch {
            when (val r = api.post(
                // clock-out devuelve el registro directo (clock-in lo devuelve dentro de {record, ...}).
                "/time-records/clock-out", SalidaRequest(), SalidaRequest.serializer(), Checada.serializer()
            )) {
                is ApiResult.Ok -> {
                    _estado.update { it.copy(enviando = false) }
                    cargar()
                }
                is ApiResult.Error -> {
                    _estado.update { it.copy(enviando = false, mensaje = textoError(r.error)) }
                    if (r.error is ApiError.Servidor && (r.error as ApiError.Servidor).codigo == 409) cargar()
                }
            }
        }
    }

    private fun textoEntrada(e: ApiError): String = when {
        e is ApiError.Servidor && e.codigo == 400 ->
            "No tienes un turno asignado hoy. Avisa a tu responsable."
        else -> textoError(e)
    }

    private fun textoError(e: ApiError): String = when (e) {
        ApiError.SinConexion -> "Sin conexión con el servidor. Intenta de nuevo."
        ApiError.NoAutorizado -> "Tu sesión ya no es válida."
        is ApiError.Limitado -> "Demasiadas peticiones. Espera un momento."
        is ApiError.Servidor ->
            if (e.codigo == 409) "Tu estado cambió, ya lo actualizamos." else "El servidor tuvo un problema (${e.codigo})."
    }
}
