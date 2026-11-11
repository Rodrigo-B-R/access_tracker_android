package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.HorarioDePersona
import com.example.access_tracker.model.Turno
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaTenue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

private val Semana = listOf("L", "M", "M", "J", "V", "S", "D")
internal val DIAS_API = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")

data class HorariosDatos(val personas: List<HorarioDePersona>, val turnos: List<Turno>)

class ResHorariosViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<HorariosDatos>>(Carga.Cargando)
    val estado: StateFlow<Carga<HorariosDatos>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val r = ServiceLocator.api.get("/schedules", ListSerializer(HorarioDePersona.serializer()))
            val c = CatalogosCache.obtener()
            _estado.value = when {
                r is ApiResult.Error -> Carga.Error(r.error.texto())
                c is ApiResult.Error -> Carga.Error(c.error.texto())
                else -> Carga.Listo(HorariosDatos((r as ApiResult.Ok).value, (c as ApiResult.Ok).value.shifts))
            }
        }
    }
}

@Composable
fun ResHorariosRoute(onAvisos: () -> Unit, onPersona: (Long) -> Unit, barra: @Composable () -> Unit = {}) {
    val vm: ResHorariosViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val sinLeer = rememberAvisosSinLeer()
    LaunchedEffect(Unit) { vm.cargar() }

    when (val e = estado) {
        is Carga.Listo -> ResHorariosContenido(e.datos, sinLeer, onAvisos, onPersona, barra)
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, barra)
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, barra, suave = true)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResHorariosContenido(
    datos: HorariosDatos,
    avisosSinLeer: Int,
    onAvisos: () -> Unit,
    onPersona: (Long) -> Unit,
    barra: @Composable () -> Unit = {},
) {
    val hoy = Fechas.hoy().dayOfWeek.value - 1
    PantallaBase(barra = barra, separacion = 16.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) { TituloPantalla("Horarios", subtitulo = "Semana tipo") }
            BotonCampana(avisosSinLeer, onAvisos)
        }
        Text(
            "Cada semana repite la anterior. Toca el nombre de una persona para cambiar sus días y turnos.",
            style = Texto.BodyS,
            color = TintaTenue
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            datos.turnos.forEachIndexed { i, t ->
                Chip("${letra(i)} · ${t.name.replaceFirstChar { it.uppercase() }}", tono = if (i == 0) TonoChip.Info else TonoChip.Alerta)
            }
            Chip("Descanso", tono = TonoChip.Neutro)
        }
        Tarjeta(radio = 24.dp, relleno = 0.dp) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.width(76.dp).height(20.dp))
                    Semana.forEachIndexed { i, dia ->
                        Box(Modifier.weight(1f).height(20.dp), contentAlignment = Alignment.Center) {
                            Text(dia, style = Texto.LabelS, color = if (i == hoy) TintaBase else TintaTenue)
                        }
                    }
                }
                datos.personas.forEach { hp ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.width(76.dp).height(36.dp).clickable { onPersona(hp.person.id) },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(abreviar(hp.person.name), style = Texto.LabelM, color = TintaBase, maxLines = 1)
                        }
                        DIAS_API.forEach { dia ->
                            val celda = hp.schedule?.days?.firstOrNull { it.weekday == dia }
                            val idx = celda?.let { c -> datos.turnos.indexOfFirst { it.id == c.shiftId } }
                            CeldaTurno(if (celda == null) '·' else letra(idx ?: 0), Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/** M para el primer turno del catalogo (matutino), V para el segundo, y asi. */
private fun letra(i: Int): Char = when (i) {
    0 -> 'M'
    1 -> 'V'
    else -> 'T'
}

/** "Ana Torres" -> "Ana T." */
private fun abreviar(nombre: String): String {
    val partes = nombre.trim().split(' ').filter { it.isNotBlank() }
    return if (partes.size < 2) nombre else "${partes[0]} ${partes[1].first()}."
}

@Composable
private fun CeldaTurno(codigo: Char, modifier: Modifier = Modifier) {
    val (fondo, tinta, texto) = when (codigo) {
        'M' -> Triple(Petroleo700, FondoCrema, "M")
        'V' -> Triple(Mostaza500, TintaBase, "V")
        'T' -> Triple(TintaBase, FondoBlanco, "T")
        else -> Triple(FondoArena, TintaTenue, "·")
    }
    Box(modifier.height(36.dp).clip(RoundedCornerShape(10.dp)).background(fondo), contentAlignment = Alignment.Center) {
        Text(texto, style = Texto.LabelM, color = tinta)
    }
}
