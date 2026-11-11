package com.example.access_tracker.screens.direccion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.Estadisticas
import com.example.access_tracker.model.Estancia
import com.example.access_tracker.model.EventoAuditoria
import com.example.access_tracker.model.ResumenDireccion
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ES = Locale.forLanguageTag("es-MX")

/** "Missing 2 in Maternal A (matutino)" (API, en ingles) -> "Faltan 2 en Maternal A". */
internal fun alertaEnEspanol(alerta: String): String {
    val m = Regex("""Missing (\d+) in (.+?) \((.+)\)""").matchEntire(alerta) ?: return alerta
    val n = m.groupValues[1].toInt()
    return (if (n == 1) "Falta 1 en " else "Faltan $n en ") + m.groupValues[2]
}

private fun fechaCorta(d: LocalDate): String =
    DateTimeFormatter.ofPattern("EEE d MMM", ES).format(d).replace(".", "").replaceFirstChar { it.uppercase(ES) }

// ── Resumen ─────────────────────────────────────────────────────────

class DirResumenViewModel : ViewModel() {
    val fecha = MutableStateFlow(Fechas.hoy())
    private val _estado = MutableStateFlow<Carga<ResumenDireccion>>(Carga.Cargando)
    val estado: StateFlow<Carga<ResumenDireccion>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _estado.value = ServiceLocator.api.get("/summary?date=${fecha.value}", ResumenDireccion.serializer()).aCarga()
        }
    }

    fun cambiarFecha(iso: String) {
        runCatching { LocalDate.parse(iso) }.getOrNull()?.let { fecha.value = it; cargar() }
    }
}

@Composable
fun DirResumenRoute(
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit,
    barra: @Composable () -> Unit,
) {
    val vm: DirResumenViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val fecha by vm.fecha.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    val datos = (estado as? Carga.Listo)?.datos
    DirResumenContenido(
        fecha = fechaCorta(fecha),
        completas = datos?.completeCount ?: 0,
        faltantes = datos?.understaffedCount ?: 0,
        estancias = datos?.daycares?.map {
            EstanciaVista(
                it.name,
                if (it.complete) "Completa" else it.alerts.firstOrNull()?.let(::alertaEnEspanol) ?: "Con faltante",
                it.complete,
            )
        }.orEmpty(),
        onFechaElegida = vm::cambiarFecha,
        onCambiarPassword = onCambiarPassword,
        onCerrarSesion = onCerrarSesion,
        barra = barra,
        mensaje = when (val e = estado) {
            is Carga.Error -> e.mensaje
            Carga.Cargando -> "Cargando…"
            is Carga.Listo -> if (e.datos.daycares.isEmpty()) "No hay estancias." else null
        },
    )
}

// ── Estadisticas ────────────────────────────────────────────────────

class DirEstadisticasViewModel : ViewModel() {
    var periodo = 0
        private set
    var estancia = 0           // 0 = todas; si no, indice en [estancias] + 1
        private set
    private val _estancias = MutableStateFlow<List<Estancia>>(emptyList())
    val estancias: StateFlow<List<Estancia>> = _estancias.asStateFlow()
    private val _estado = MutableStateFlow<Carga<Estadisticas>>(Carga.Cargando)
    val estado: StateFlow<Carga<Estadisticas>> = _estado.asStateFlow()
    private val _periodoUi = MutableStateFlow(0)
    val periodoUi: StateFlow<Int> = _periodoUi.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            if (_estancias.value.isEmpty()) {
                val r = ServiceLocator.api.get("/daycares", ListSerializer(Estancia.serializer()))
                if (r is ApiResult.Ok) _estancias.value = r.value
            }
            pedir()
        }
    }

    fun cambiarPeriodo(i: Int) {
        periodo = i
        _periodoUi.value = i
        viewModelScope.launch { pedir() }
    }

    fun cambiarEstancia(i: Int) {
        estancia = i
        viewModelScope.launch { pedir() }
    }

    private suspend fun pedir() {
        _estado.value = Carga.Cargando
        val hoy = Fechas.hoy()
        val desde = when (periodo) {
            0 -> hoy.withDayOfMonth(1)
            1 -> hoy.minusMonths(2).withDayOfMonth(1)
            else -> hoy.withDayOfYear(1)
        }
        val id = _estancias.value.getOrNull(estancia - 1)?.id
        val extra = if (estancia > 0 && id != null) "&daycareId=$id" else ""
        _estado.value = ServiceLocator.api
            .get("/statistics?startDate=$desde&endDate=$hoy$extra", Estadisticas.serializer()).aCarga()
    }
}

@Composable
fun DirEstadisticasRoute(
    onExportar: () -> Unit,
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit,
    barra: @Composable () -> Unit,
) {
    val vm: DirEstadisticasViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val estancias by vm.estancias.collectAsState()
    val periodo by vm.periodoUi.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    val d = (estado as? Carga.Listo)?.datos
    DirEstadisticasContenido(
        periodo = periodo,
        onPeriodo = vm::cambiarPeriodo,
        estancias = listOf("Todas") + estancias.map { it.name },
        onEstancia = vm::cambiarEstancia,
        altas = d?.hires?.toString() ?: "–",
        bajas = d?.terminations?.toString() ?: "–",
        insuficiente = d?.let { "${it.understaffedDays} días" } ?: "–",
        absentismo = d?.let { String.format(ES, "%.1f %%", it.absenteeism) } ?: "–",
        barras = d?.byDaycare?.map { BarrasEstancia(it.daycare.name, it.hires, it.terminations) }.orEmpty(),
        onExportar = onExportar,
        onCambiarPassword = onCambiarPassword,
        onCerrarSesion = onCerrarSesion,
        barra = barra,
        mensaje = when (val e = estado) {
            is Carga.Error -> e.mensaje
            Carga.Cargando -> "Cargando…"
            else -> null
        },
    )
}

// ── Bitacora ────────────────────────────────────────────────────────

private val TABLAS = listOf(null, "registro_entradas", "asistencia", "correccion_registro")
private val FILTROS = listOf("Todo", "Checadas", "Asistencia", "Correcciones")

class DirBitacoraViewModel : ViewModel() {
    var filtro = 0
        private set
    private val _filtroUi = MutableStateFlow(0)
    val filtroUi: StateFlow<Int> = _filtroUi.asStateFlow()
    private val _estado = MutableStateFlow<Carga<List<EventoAuditoria>>>(Carga.Cargando)
    val estado: StateFlow<Carga<List<EventoAuditoria>>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _estado.value = Carga.Cargando
            val hoy = Fechas.hoy()
            val tabla = TABLAS[filtro]?.let { "&table=$it" }.orEmpty()
            _estado.value = ServiceLocator.api.get(
                "/audit?startDate=${hoy.minusDays(14)}&endDate=$hoy$tabla",
                ListSerializer(EventoAuditoria.serializer()),
            ).aCarga()
        }
    }

    fun cambiarFiltro(i: Int) {
        filtro = i
        _filtroUi.value = i
        cargar()
    }
}

internal fun EventoAuditoria.aVista(): EventoVista {
    val nuevo = action == "INSERT"
    val (icono, fondo, tipo, tono) = when (table) {
        "registro_entradas" -> Cuatro(Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info)
        "asistencia" -> Cuatro(Ico.Alert, Coral100, "Asistencia", TonoChip.Coral)
        else -> Cuatro(Ico.Swap, Mostaza100, "Corrección", TonoChip.Alerta)
    }
    val titulo = when (table) {
        "registro_entradas" -> if (nuevo) "Registro creado" else "Registro modificado"
        "asistencia" -> if (nuevo) "Asistencia registrada" else "Asistencia corregida"
        else -> if (nuevo) "Corrección solicitada" else "Corrección resuelta"
    }
    val cuando = runCatching {
        DateTimeFormatter.ofPattern("d MMM H:mm", ES)
            .format(java.time.Instant.parse(createdAt).atZone(Fechas.ZONA)).replace(".", "")
    }.getOrDefault("")
    return EventoVista(icono, fondo, tipo, tono, titulo, "Registrado por ${user?.name ?: "el sistema"} · $cuando")
}

private data class Cuatro(val a: Ico, val b: androidx.compose.ui.graphics.Color, val c: String, val d: TonoChip)

@Composable
fun DirBitacoraRoute(
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit,
    barra: @Composable () -> Unit,
) {
    val vm: DirBitacoraViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val filtro by vm.filtroUi.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    DirBitacoraContenido(
        filtros = FILTROS,
        filtro = filtro,
        onFiltro = vm::cambiarFiltro,
        eventos = (estado as? Carga.Listo)?.datos?.map { it.aVista() }.orEmpty(),
        onCambiarPassword = onCambiarPassword,
        onCerrarSesion = onCerrarSesion,
        barra = barra,
        mensaje = when (val e = estado) {
            is Carga.Error -> e.mensaje
            Carga.Cargando -> "Cargando…"
            is Carga.Listo -> if (e.datos.isEmpty()) "Sin cambios en los últimos 14 días." else null
        },
    )
}
