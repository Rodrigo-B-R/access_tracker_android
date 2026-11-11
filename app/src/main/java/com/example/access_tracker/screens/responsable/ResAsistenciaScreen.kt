package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.BotonFlotanteIcono
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.FiltroDesplegable
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.AsistenciaPersona
import com.example.access_tracker.model.Turno
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Texto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

/** Lo que el responsable necesita ver del dia: la gente y los turnos del catalogo. */
data class AsistenciaDia(val personas: List<AsistenciaPersona>, val turnos: List<Turno>)

class ResAsistenciaViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<AsistenciaDia>>(Carga.Cargando)
    val estado: StateFlow<Carga<AsistenciaDia>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val hoy = Fechas.hoy()
            val cat = CatalogosCache.obtener()
            val r = ServiceLocator.api.get(
                "/attendance?startDate=$hoy&endDate=$hoy", ListSerializer(AsistenciaPersona.serializer())
            )
            _estado.value = when {
                r is ApiResult.Error -> Carga.Error(r.error.texto())
                cat is ApiResult.Error -> Carga.Error(cat.error.texto())
                else -> Carga.Listo(
                    AsistenciaDia((r as ApiResult.Ok).value, (cat as ApiResult.Ok).value.shifts)
                )
            }
        }
    }
}

private data class FilaDia(
    val persona: AsistenciaPersona,
    val estado: String,
    val sala: String?,
    val turnoId: Long?,
    val entrada: String?,
)

private fun AsistenciaPersona.fila(): FilaDia? {
    val d = days.firstOrNull() ?: return null
    if (d.status == "day_off" || d.status == "scheduled") return null
    val t = d.shifts.firstOrNull()
    return FilaDia(this, d.status, t?.room?.name, t?.shiftId, d.clockIn)
}

private fun etiquetaEstado(estado: String): Pair<String, TonoChip> = when (estado) {
    "present" -> "Presente" to TonoChip.Exito
    "late" -> "Retardo" to TonoChip.Alerta
    "absent" -> "Ausente" to TonoChip.Peligro
    "incident" -> "Incidencia" to TonoChip.Alerta
    "no_record" -> "Sin checar" to TonoChip.Neutro
    "pending" -> "Pendiente" to TonoChip.Neutro
    "outside_schedule" -> "Fuera de horario" to TonoChip.Info
    else -> estado to TonoChip.Neutro
}

@Composable
fun ResAsistenciaRoute(
    onAvisos: () -> Unit,
    onTurnoCompleto: () -> Unit,
    onAusencia: (userId: Long?) -> Unit,
    barra: @Composable () -> Unit = {},
) {
    val vm: ResAsistenciaViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val sinLeer = rememberAvisosSinLeer()
    LaunchedEffect(Unit) { vm.cargar() }

    when (val e = estado) {
        is Carga.Listo -> ResAsistenciaContenido(e.datos, sinLeer, onAvisos, onTurnoCompleto, onAusencia, barra)
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, barra)
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, barra, suave = true)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResAsistenciaContenido(
    datos: AsistenciaDia,
    avisosSinLeer: Int,
    onAvisos: () -> Unit,
    onTurnoCompleto: () -> Unit,
    onAusencia: (userId: Long?) -> Unit,
    barra: @Composable () -> Unit = {},
) {
    var turno by remember { mutableIntStateOf(0) }
    var salaIdx by remember { mutableIntStateOf(0) }
    val filas = remember(datos) { datos.personas.mapNotNull { it.fila() } }
    val salas = remember(filas) { listOf("Todas las salas") + filas.mapNotNull { it.sala }.distinct().sorted() }
    val turnoId = datos.turnos.getOrNull(turno)?.id
    val visibles = filas.filter { f ->
        (turnoId == null || f.turnoId == turnoId || f.turnoId == null) &&
            (salaIdx == 0 || f.sala == salas.getOrNull(salaIdx))
    }
    PantallaBase(
        barra = barra,
        separacion = 16.dp,
        botonFlotante = { BotonFlotanteIcono(Ico.Plus, Coral500, { onAusencia(null) }, Modifier.padding(end = 8.dp)) },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) { TituloPantalla("Asistencia", subtitulo = Fechas.fechaLarga()) }
            BotonCampana(avisosSinLeer, onAvisos)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (datos.turnos.isNotEmpty()) {
                Segmentado(
                    datos.turnos.map { it.name.replaceFirstChar { c -> c.uppercase() } },
                    turno, { turno = it }, Modifier.weight(1f)
                )
            }
            FiltroDesplegable(salas.first(), salas, onElegido = { salaIdx = it })
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Chip("${visibles.count { it.estado == "present" }} presentes", tono = TonoChip.Exito)
            Chip("${visibles.count { it.estado == "late" }} retardos", tono = TonoChip.Alerta)
            Chip("${visibles.count { it.estado == "no_record" || it.estado == "pending" }} sin checar", tono = TonoChip.Neutro)
            Chip("${visibles.count { it.estado == "absent" || it.estado == "incident" }} ausentes", tono = TonoChip.Peligro)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Petroleo700)
                .clickable(onClick = onTurnoCompleto)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Users, tint = Mostaza500)
            Text(
                "Registrar entrada de turno completo",
                style = Texto.LabelL,
                color = FondoCrema,
                modifier = Modifier.weight(1f)
            )
            Icono(Ico.Chevron, tint = FondoCrema, tamano = 20.dp)
        }
        if (visibles.isEmpty()) {
            Text("No hay personal programado con este filtro.", style = Texto.BodyM, color = com.example.access_tracker.ui.theme.TintaSuave)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            visibles.forEach { f ->
                val p = f.persona.person
                val (fondo, tinta) = paleta(p.id)
                val (etiqueta, tono) = etiquetaEstado(f.estado)
                val detalle = listOfNotNull(
                    f.sala ?: p.position?.name,
                    f.entrada?.let { "Entrada ${com.example.access_tracker.data.Fechas.horaCorta(it)}" },
                ).joinToString(" · ")
                FilaPersona(
                    p.iniciales, p.name, detalle,
                    fondoAvatar = fondo, tintaAvatar = tinta,
                    onClick = { onAusencia(p.id) },
                ) { Chip(etiqueta, tono = tono) }
            }
        }
    }
}
