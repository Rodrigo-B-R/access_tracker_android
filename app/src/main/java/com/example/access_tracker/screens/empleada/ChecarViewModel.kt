package com.example.access_tracker.screens.empleada

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.access_tracker.data.ApiError
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.Gps
import com.example.access_tracker.data.Pendiente
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
import java.time.Instant
import java.time.temporal.ChronoUnit
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
    /** Checadas guardadas en este telefono que todavia no llegan al servidor, en orden. */
    val pendientes: List<Pendiente> = emptyList(),
    /** No se pudo llegar al servidor: [datos] viene de la ultima copia guardada. */
    val sinConexion: Boolean = false,
    /** La copia guardada es de otro dia: no se sabe cual es el turno de hoy. */
    val datosDeOtroDia: Boolean = false,
)

class ChecarViewModel(app: Application) : AndroidViewModel(app) {
    private val api = ServiceLocator.api
    private val cola = ServiceLocator.cola
    private val cache = ServiceLocator.cache
    private val sincronizador = ServiceLocator.sincronizador

    private val _estado = MutableStateFlow(ChecarEstado())
    val estado: StateFlow<ChecarEstado> = _estado.asStateFlow()

    /**
     * uuid de la entrada en curso. Se reutiliza si el intento falla, para que un reintento
     * nunca duplique la checada (la API responde repeated=true si ya la tenia).
     */
    private var uuidPendiente: String? = null

    /** Ahora en ISO-8601 UTC con milisegundos (los decimales extra de Instant podrian no gustarle a la API). */
    private fun ahoraIso(): String = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString()

    private fun usuarioId(): Long? = ServiceLocator.sesion.leer()?.userId

    fun cargar() {
        _estado.update { it.copy(cargando = it.datos == null, errorCarga = null) }
        viewModelScope.launch {
            val usuario = usuarioId()
            // Primero se manda lo que se guardo sin internet (si ya hay conexion, sale ahora).
            val sync = sincronizador.sincronizar()
            refrescarPendientes(usuario, mostrarRechazos = true)

            // Sin conexion ya comprobada: no se gasta otro tiempo de espera en /status.
            if (sync.sinConexion) {
                usarCopiaGuardada(usuario)
                return@launch
            }
            when (val r = api.get("/time-records/status", EstadoChecada.serializer())) {
                is ApiResult.Ok -> {
                    if (usuario != null) cache.guardar(usuario, Fechas.hoy().toString(), r.value)
                    _estado.update {
                        it.copy(cargando = false, datos = r.value, sinConexion = false, datosDeOtroDia = false)
                    }
                }
                is ApiResult.Error ->
                    if (r.error == ApiError.SinConexion) usarCopiaGuardada(usuario)
                    else _estado.update { it.copy(cargando = false, errorCarga = textoError(r.error)) }
            }
            // El contador es secundario: si falla, se deja el que haya.
            val avisos = api.get("/notices/inbox", ListSerializer(Aviso.serializer()))
            if (avisos is ApiResult.Ok) {
                _estado.update { it.copy(avisosSinLeer = avisos.value.count { a -> !a.read }) }
            }
        }
    }

    /** Sin internet: se muestra el ultimo estado guardado para poder seguir checando. */
    private fun usarCopiaGuardada(usuario: Long?) {
        val copia = usuario?.let { cache.leer(it) }
        _estado.update {
            when {
                copia != null -> it.copy(
                    cargando = false, datos = copia.estado, sinConexion = true,
                    datosDeOtroDia = copia.fecha != Fechas.hoy().toString(),
                )
                it.datos != null -> it.copy(cargando = false, sinConexion = true)
                else -> it.copy(
                    cargando = false,
                    errorCarga = "Sin conexión con el servidor. Conéctate una vez para cargar tu turno.",
                )
            }
        }
    }

    private fun refrescarPendientes(usuario: Long?, mostrarRechazos: Boolean = false) {
        if (usuario == null) return
        val rechazos = if (mostrarRechazos) cola.rechazos(usuario) else emptyList()
        if (rechazos.isNotEmpty()) cola.limpiarRechazos(usuario)
        _estado.update {
            it.copy(
                pendientes = cola.pendientes(usuario),
                mensaje = if (rechazos.isNotEmpty()) textoRechazos(rechazos) else it.mensaje,
            )
        }
    }

    private fun textoRechazos(rechazos: List<com.example.access_tracker.data.Rechazo>): String =
        rechazos.joinToString("\n") {
            "Tu ${it.tipo} de las ${Fechas.horaLocal(it.ocurridoEn)} no se pudo registrar: ${it.motivo}. " +
                "Avisa a tu responsable."
        }

    fun registrarEntrada() {
        val usuario = usuarioId() ?: return
        if (_estado.value.enviando) return
        val ocurridoEn = ahoraIso() // hora real del toque, aunque tarde en enviarse
        _estado.update { it.copy(enviando = true, mensaje = null) }
        viewModelScope.launch {
            val uuid = uuidPendiente ?: UUID.randomUUID().toString().also { uuidPendiente = it }
            val ubicacion = Gps.actual(getApplication())
            // Sin ubicacion NO se manda withinZone=true: la API lo daria por valido. Se marca fuera de zona.
            val pendiente = Pendiente(
                uuid = uuid, tipo = Pendiente.TIPO_ENTRADA, usuarioId = usuario, ocurridoEn = ocurridoEn,
                latitud = ubicacion?.latitude, longitud = ubicacion?.longitude,
                enZona = if (ubicacion == null) false else null,
            )
            val avisoSinUbicacion = if (ubicacion == null) {
                "No se pudo leer tu ubicación: la entrada quedó como fuera de zona."
            } else null

            // Si ya hay checadas en cola (o se sabe que no hay red), esta va detras para conservar el orden.
            if (hayColaOSinRed(usuario)) {
                encolar(pendiente, avisoSinUbicacion)
                return@launch
            }
            val cuerpo = EntradaRequest(uuid, pendiente.latitud, pendiente.longitud, pendiente.enZona)
            when (val r = api.post(
                "/time-records/clock-in", cuerpo, EntradaRequest.serializer(), RespuestaChecada.serializer()
            )) {
                is ApiResult.Ok -> {
                    uuidPendiente = null
                    _estado.update { it.copy(enviando = false, mensaje = avisoSinUbicacion) }
                    cargar()
                }
                is ApiResult.Error -> {
                    val e = r.error
                    if (e == ApiError.SinConexion) {
                        // Se guarda con la hora del toque y el mismo uuid: al reconectar no se duplica.
                        encolar(pendiente, avisoSinUbicacion)
                    } else {
                        // 400/409 los responde la API (sin turno, entrada ya abierta): no se reintenta.
                        if (e is ApiError.Servidor) uuidPendiente = null
                        _estado.update { it.copy(enviando = false, mensaje = textoEntrada(e)) }
                        if (e is ApiError.Servidor && e.codigo == 409) cargar()
                    }
                }
            }
        }
    }

    fun registrarSalida() {
        val usuario = usuarioId() ?: return
        if (_estado.value.enviando) return
        val ocurridoEn = ahoraIso()
        _estado.update { it.copy(enviando = true, mensaje = null) }
        viewModelScope.launch {
            val pendiente = Pendiente(
                uuid = UUID.randomUUID().toString(), tipo = Pendiente.TIPO_SALIDA,
                usuarioId = usuario, ocurridoEn = ocurridoEn,
            )
            if (hayColaOSinRed(usuario)) {
                encolar(pendiente, null)
                return@launch
            }
            when (val r = api.post(
                // clock-out devuelve el registro directo (clock-in lo devuelve dentro de {record, ...}).
                "/time-records/clock-out", SalidaRequest(), SalidaRequest.serializer(), Checada.serializer()
            )) {
                is ApiResult.Ok -> {
                    _estado.update { it.copy(enviando = false) }
                    cargar()
                }
                is ApiResult.Error -> {
                    if (r.error == ApiError.SinConexion) {
                        encolar(pendiente, null)
                    } else {
                        _estado.update { it.copy(enviando = false, mensaje = textoError(r.error)) }
                        if (r.error is ApiError.Servidor && (r.error as ApiError.Servidor).codigo == 409) cargar()
                    }
                }
            }
        }
    }

    private fun hayColaOSinRed(usuario: Long) =
        cola.pendientes(usuario).isNotEmpty() || _estado.value.sinConexion

    /** Guarda la checada en el telefono, pide el reintento automatico y prueba enviar ya por si hay red. */
    private fun encolar(p: Pendiente, aviso: String?) {
        uuidPendiente = null
        cola.agregar(p)
        sincronizador.programar()
        _estado.update {
            it.copy(enviando = false, mensaje = aviso, pendientes = cola.pendientes(p.usuarioId), sinConexion = true)
        }
        // En segundo plano: si en realidad ya hay red, se envia y la pantalla se actualiza sola.
        viewModelScope.launch {
            val r = sincronizador.sincronizar()
            if (!r.quedanPendientes) cargar() else refrescarPendientes(p.usuarioId)
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
