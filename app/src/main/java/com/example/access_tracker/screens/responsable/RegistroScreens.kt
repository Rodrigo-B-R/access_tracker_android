package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoFecha
import com.example.access_tracker.components.CampoHora
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.Casilla
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.AsistenciaPersona
import com.example.access_tracker.model.AsistenciaRegistrada
import com.example.access_tracker.model.AsistenciaRequest
import com.example.access_tracker.model.Catalogos
import com.example.access_tracker.model.HorarioDePersona
import com.example.access_tracker.model.LoteRequest
import com.example.access_tracker.model.Persona
import com.example.access_tracker.model.RespuestaLote
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import java.time.LocalDate

// ── Entrada de turno completo ───────────────────────────────────────

/** Una persona programada hoy en un turno; [yaRegistrada] trae la hora si ya checo. */
data class ProgramadaHoy(val persona: Persona, val sala: String, val turnoId: Long, val yaRegistrada: String?)

data class DatosTurno(val catalogos: Catalogos, val programadas: List<ProgramadaHoy>)

class RegistroTurnoViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<DatosTurno>>(Carga.Cargando)
    val estado: StateFlow<Carga<DatosTurno>> = _estado.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val hoy = Fechas.hoy()
            val clave = DIAS_API[hoy.dayOfWeek.value - 1]
            val c = CatalogosCache.obtener()
            val h = ServiceLocator.api.get("/schedules", ListSerializer(HorarioDePersona.serializer()))
            val a = ServiceLocator.api.get(
                "/attendance?startDate=$hoy&endDate=$hoy", ListSerializer(AsistenciaPersona.serializer())
            )
            _estado.value = when {
                c is ApiResult.Error -> Carga.Error(c.error.texto())
                h is ApiResult.Error -> Carga.Error(h.error.texto())
                a is ApiResult.Error -> Carga.Error(a.error.texto())
                else -> {
                    val entradas = (a as ApiResult.Ok).value.associate { it.person.id to it.days.firstOrNull()?.clockIn }
                    val lista = (h as ApiResult.Ok).value.flatMap { hp ->
                        val dia = hp.schedule?.days?.firstOrNull { it.weekday == clave } ?: return@flatMap emptyList()
                        val persona = Persona(hp.person.id, hp.person.name, position = hp.person.position)
                        listOf(ProgramadaHoy(persona, dia.room.name, dia.shiftId, entradas[hp.person.id]))
                    }
                    Carga.Listo(DatosTurno((c as ApiResult.Ok).value, lista))
                }
            }
        }
    }

    fun registrar(ids: List<Long>, hora: Pair<Int, Int>?) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            val cuando = hora?.let { "%sT%02d:%02d:00".format(Fechas.hoy(), it.first, it.second) }
            val r = ServiceLocator.api.post("/time-records/batch", LoteRequest(ids, cuando), LoteRequest.serializer(), RespuestaLote.serializer())
            _accion.value = when (r) {
                is ApiResult.Error -> Accion(error = r.error.textoAccion())
                is ApiResult.Ok ->
                    if (r.value.skipped.isEmpty()) Accion(listo = true)
                    else Accion(error = "Se registraron ${r.value.recorded.size}; ${r.value.skipped.size} se omitieron (ya tenían entrada o no aplican).")
            }
        }
    }
}

@Composable
fun RegistroTurnoRoute(onCerrar: () -> Unit) {
    val vm: RegistroTurnoViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val accion by vm.accion.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(accion.listo) { if (accion.listo) onCerrar() }

    when (val e = estado) {
        is Carga.Listo -> RegistroTurnoContenido(e.datos, accion, onCerrar) { ids, hora -> vm.registrar(ids, hora) }
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, {})
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, {}, suave = true)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegistroTurnoContenido(
    datos: DatosTurno,
    accion: Accion,
    onCerrar: () -> Unit,
    onRegistrar: (List<Long>, Pair<Int, Int>?) -> Unit,
) {
    val turnos = datos.catalogos.shifts
    var turno by remember { mutableIntStateOf(0) }
    var hora by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    val turnoId = turnos.getOrNull(turno)?.id
    val delTurno = datos.programadas.filter { it.turnoId == turnoId }
    val pendientes = delTurno.filter { it.yaRegistrada == null }
    // Marcadas por id; se reinicia al cambiar de turno.
    val marcados: SnapshotStateList<Long> = remember(turno) { mutableStateListOf<Long>().apply { addAll(pendientes.map { it.persona.id }) } }
    val todos = pendientes.isNotEmpty() && marcados.size == pendientes.size

    PantallaBase(
        pie = {
            Boton(
                if (accion.enviando) "Registrando…" else "Registrar entrada de ${marcados.size} personas",
                { onRegistrar(marcados.toList(), hora) },
                estilo = EstiloBoton.Oscuro,
                habilitado = marcados.isNotEmpty() && !accion.enviando,
            )
        },
    ) {
        EncabezadoPantalla("Entrada de turno completo", onAtras = onCerrar, cerrar = true, eyebrow = "Asistencia · hoy")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Turno", style = Texto.LabelM, color = TintaSuave)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                turnos.forEachIndexed { i, t ->
                    ChipOpcion(
                        t.name.replaceFirstChar { it.uppercase() }, Modifier.weight(1f),
                        seleccionado = turno == i, onClick = { turno = i }
                    )
                }
            }
        }
        CampoHora("Hora de entrada", "Ahora", onHora = { h, m -> hora = h to m })
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Petroleo100)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Seleccionar todos", style = Texto.LabelL, color = Petroleo700, modifier = Modifier.weight(1f))
            Text("${marcados.size} de ${pendientes.size}", style = Texto.LabelM, color = Petroleo700)
            Casilla(todos, onClick = {
                if (todos) marcados.clear() else { marcados.clear(); marcados.addAll(pendientes.map { it.persona.id }) }
            })
        }
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
        if (delTurno.isEmpty()) Text("Nadie está programado hoy en este turno.", style = Texto.BodyM, color = TintaSuave)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            delTurno.forEach { p ->
                val (fondo, tinta) = paleta(p.persona.id)
                if (p.yaRegistrada == null) {
                    FilaPersona(
                        p.persona.iniciales, p.persona.name, p.sala,
                        fondoAvatar = fondo, tintaAvatar = tinta, ladoAvatar = 40.dp
                    ) {
                        Casilla(p.persona.id in marcados, onClick = {
                            if (p.persona.id in marcados) marcados.remove(p.persona.id) else marcados.add(p.persona.id)
                        })
                    }
                } else {
                    FilaPersona(
                        p.persona.iniciales, p.persona.name,
                        "${p.sala} · ya registrada ${Fechas.horaCorta(p.yaRegistrada)}",
                        ladoAvatar = 40.dp, fondo = FondoArena
                    ) { Casilla(marcada = true, bloqueada = true) }
                }
            }
        }
    }
}

// ── Ausencia o incidencia ───────────────────────────────────────────

data class DatosAusencia(val personal: List<Persona>, val motivos: List<com.example.access_tracker.model.MotivoAusencia>)

class RegistrarAusenciaViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<DatosAusencia>>(Carga.Cargando)
    val estado: StateFlow<Carga<DatosAusencia>> = _estado.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val c = CatalogosCache.obtener()
            val p = ServiceLocator.api.get("/staff", ListSerializer(Persona.serializer()))
            _estado.value = when {
                c is ApiResult.Error -> Carga.Error(c.error.texto())
                p is ApiResult.Error -> Carga.Error(p.error.texto())
                else -> Carga.Listo(DatosAusencia((p as ApiResult.Ok).value, (c as ApiResult.Ok).value.absenceReasons))
            }
        }
    }

    fun guardar(userId: Long, fecha: LocalDate, incidencia: Boolean, motivoId: Long) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            val body = AsistenciaRequest(userId, fecha.toString(), if (incidencia) "incident" else "absent", motivoId)
            _accion.value = when (
                val r = ServiceLocator.api.post("/attendance", body, AsistenciaRequest.serializer(), AsistenciaRegistrada.serializer())
            ) {
                is ApiResult.Ok -> Accion(listo = true)
                is ApiResult.Error -> Accion(error = r.error.textoAccion())
            }
        }
    }
}

@Composable
fun RegistrarAusenciaRoute(userId: Long?, onCerrar: () -> Unit) {
    val vm: RegistrarAusenciaViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val accion by vm.accion.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(accion.listo) { if (accion.listo) onCerrar() }

    when (val e = estado) {
        is Carga.Listo -> AusenciaContenido(e.datos, userId, accion, onCerrar) { u, f, i, m -> vm.guardar(u, f, i, m) }
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, {})
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, {}, suave = true)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AusenciaContenido(
    datos: DatosAusencia,
    userId: Long?,
    accion: Accion,
    onCerrar: () -> Unit,
    onGuardar: (Long, LocalDate, Boolean, Long) -> Unit,
) {
    var persona by remember { mutableIntStateOf(datos.personal.indexOfFirst { it.id == userId }.coerceAtLeast(0)) }
    var fecha by remember { mutableStateOf(Fechas.hoy()) }
    var tipo by remember { mutableIntStateOf(0) }
    var motivo by remember { mutableIntStateOf(0) }
    val p = datos.personal.getOrNull(persona)

    PantallaBase(
        separacion = 20.dp,
        pie = {
            Boton(
                if (accion.enviando) "Guardando…" else "Guardar registro",
                {
                    val m = datos.motivos.getOrNull(motivo)
                    if (p != null && m != null) onGuardar(p.id, fecha, tipo == 1, m.id)
                },
                estilo = EstiloBoton.Oscuro,
                habilitado = p != null && datos.motivos.isNotEmpty() && !accion.enviando,
            )
        },
    ) {
        EncabezadoPantalla("Registrar ausencia o incidencia", onAtras = onCerrar, cerrar = true)
        if (p == null) {
            Text("No hay personal activo.", style = Texto.BodyM, color = TintaSuave)
        } else {
            CampoOpciones(
                "Persona", p.name, datos.personal.map { it.name },
                inicio = { Avatar(p.iniciales, lado = 28.dp) },
                onElegido = { persona = it },
            )
        }
        CampoFecha("Fecha", Fechas.fechaLarga(fecha), onFecha = { fecha = it })
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tipo", style = Texto.LabelM, color = TintaSuave)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Falta", "Incidencia").forEachIndexed { i, t ->
                    ChipOpcion(t, Modifier.weight(1f), seleccionado = tipo == i, onClick = { tipo = i })
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Motivo (categoría general)", style = Texto.LabelM, color = TintaSuave)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                datos.motivos.forEachIndexed { i, m ->
                    ChipOpcion(m.description, seleccionado = motivo == i, onClick = { motivo = i })
                }
            }
        }
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
        Nota(
            "Solo la Responsable ve los motivos. No captures diagnósticos ni datos de niñas y niños.",
            icono = Ico.Shield
        )
    }
}
