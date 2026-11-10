package com.example.access_tracker.screens.empleada

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.AcuseLectura
import com.example.access_tracker.model.Aviso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

/** (etiqueta, tono) de cada tipo de aviso de la API. */
internal fun etiquetaDe(tipo: String): Pair<String, TonoChip> = when (tipo) {
    "urgent" -> "Urgente" to TonoChip.Peligro
    "schedule_change" -> "Cambio de horario" to TonoChip.Alerta
    "meeting" -> "Reunión" to TonoChip.Info
    "reminder" -> "Recordatorio" to TonoChip.Neutro
    else -> "Aviso" to TonoChip.Neutro
}

internal fun Aviso.aVista(): AvisoVista {
    val (etiqueta, tono) = etiquetaDe(noticeType)
    return AvisoVista(
        id = id, etiqueta = etiqueta, tono = tono, urgente = urgente,
        de = "De: ${author.name.substringBefore(' ')}",
        titulo = subject, texto = message, leido = read,
        requiereConfirmacion = requiresConfirmation,
    )
}

// ── Bandeja ─────────────────────────────────────────────────────────

class AvisosViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<List<Aviso>>>(Carga.Cargando)
    val estado: StateFlow<Carga<List<Aviso>>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _estado.value = ServiceLocator.api
                .get("/notices/inbox", ListSerializer(Aviso.serializer())).aCarga()
        }
    }
}

@Composable
fun AvisosRoute(onAtras: () -> Unit, onAbrir: (Long) -> Unit) {
    val vm: AvisosViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    // Se recarga al volver del detalle para que se actualice "sin leer".
    LaunchedEffect(Unit) { vm.cargar() }

    when (val e = estado) {
        is Carga.Listo -> AvisosBandejaContenido(
            avisos = e.datos.map { it.aVista() },
            onAtras = onAtras,
            onAbrir = onAbrir,
            mensaje = if (e.datos.isEmpty()) "No tienes avisos." else null,
        )
        is Carga.Error -> AvisosBandejaContenido(emptyList(), onAtras, onAbrir, mensaje = e.mensaje)
        Carga.Cargando -> AvisosBandejaContenido(emptyList(), onAtras, onAbrir, mensaje = "Cargando…")
    }
}

// ── Detalle ─────────────────────────────────────────────────────────

class AvisoDetalleViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<Aviso>>(Carga.Cargando)
    val estado: StateFlow<Carga<Aviso>> = _estado.asStateFlow()

    private val _enviando = MutableStateFlow(false)
    val enviando: StateFlow<Boolean> = _enviando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            val r = ServiceLocator.api.get("/notices/inbox/$id", Aviso.serializer())
            _estado.value = r.aCarga()
            // Si no pide confirmacion, abrirlo ya cuenta como leido (quita el "sin leer").
            if (r is ApiResult.Ok && !r.value.read && !r.value.requiresConfirmation) marcarLeido(id, silencioso = true)
        }
    }

    fun confirmar(id: Long) {
        if (_enviando.value) return
        _enviando.value = true
        _error.value = null
        viewModelScope.launch {
            marcarLeido(id, silencioso = false)
            _enviando.value = false
        }
    }

    private suspend fun marcarLeido(id: Long, silencioso: Boolean) {
        when (val r = ServiceLocator.api.post("/notices/$id/read", "{}", AcuseLectura.serializer())) {
            is ApiResult.Ok -> _estado.update { c ->
                if (c is Carga.Listo) Carga.Listo(c.datos.copy(read = r.value.read, readAt = r.value.readAt)) else c
            }
            is ApiResult.Error -> if (!silencioso) _error.value = r.error.texto()
        }
    }
}

@Composable
fun AvisoRoute(id: Long, onAtras: () -> Unit) {
    val vm: AvisoDetalleViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val enviando by vm.enviando.collectAsState()
    val error by vm.error.collectAsState()
    LaunchedEffect(id) { vm.cargar(id) }

    when (val e = estado) {
        is Carga.Listo -> {
            val a = e.datos
            AvisoDetalleContenido(
                etiqueta = etiquetaDe(a.noticeType).first,
                urgente = a.urgente,
                titulo = a.subject,
                autor = a.author.name,
                mensaje = a.message,
                requiereConfirmacion = a.requiresConfirmation,
                leido = a.read,
                enviando = enviando,
                onAtras = onAtras,
                onConfirmar = { vm.confirmar(id) },
                error = error,
            )
        }
        is Carga.Error -> MensajePantalla(e.mensaje, { vm.cargar(id) }, {})
        Carga.Cargando -> MensajePantalla("Cargando…", null, {}, suave = true)
    }
}
