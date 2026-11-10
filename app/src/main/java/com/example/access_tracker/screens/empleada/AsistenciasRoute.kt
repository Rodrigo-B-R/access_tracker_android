package com.example.access_tracker.screens.empleada

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.model.DiaAsistenciaDto
import com.example.access_tracker.model.MiAsistencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class AsistenciasViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<MiAsistencia>>(Carga.Cargando)
    val estado: StateFlow<Carga<MiAsistencia>> = _estado.asStateFlow()

    /** [periodo]: 0 = esta semana, 1 = semana pasada, 2 = mes en curso. */
    fun cargar(periodo: Int) {
        val hoy = Fechas.hoy()
        val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val (desde, hasta) = when (periodo) {
            0 -> lunes to hoy
            1 -> lunes.minusWeeks(1) to lunes.minusDays(1)
            else -> hoy.withDayOfMonth(1) to hoy
        }
        _estado.value = Carga.Cargando
        viewModelScope.launch {
            _estado.value = ServiceLocator.api
                .get("/attendance/me?startDate=$desde&endDate=$hasta", MiAsistencia.serializer())
                .aCarga()
        }
    }
}

private val DIAS_CORTOS = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")

/** Convierte un dia de la API en una fila; null = no se muestra (descanso o dia futuro). */
internal fun aVista(d: DiaAsistenciaDto, hoy: LocalDate): AsistenciaVista? {
    val fecha = LocalDate.parse(d.date)
    val (estado, tono) = when (d.status) {
        "present" -> "A tiempo" to TonoChip.Exito
        "late" -> "Retardo ${d.lateMinutes} min" to TonoChip.Alerta
        "absent" -> "Falta" to TonoChip.Peligro
        "incident" -> "Incidencia" to TonoChip.Coral
        "no_record" -> "Sin registro" to TonoChip.Peligro
        "pending" -> "Pendiente" to TonoChip.Neutro
        "outside_schedule" -> "Fuera de horario" to TonoChip.Info
        else -> return null // day_off, scheduled
    }
    val detalle = when {
        d.clockIn != null -> {
            val salida = d.clockOut?.let { "Salida ${Fechas.horaCorta(it)}" } ?: "Salida en curso"
            "Entrada ${Fechas.horaCorta(d.clockIn)} · $salida"
        }
        d.reason != null -> d.reason
        else -> "Sin entrada registrada"
    }
    return AsistenciaVista(
        dia = DIAS_CORTOS[fecha.dayOfWeek.value - 1],
        numero = fecha.dayOfMonth.toString(),
        estado = estado,
        tono = tono,
        detalle = detalle,
        actual = fecha == hoy,
    )
}

@Composable
fun AsistenciasRoute(barra: @Composable () -> Unit) {
    val vm: AsistenciasViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    var periodo by remember { mutableIntStateOf(0) }
    LaunchedEffect(periodo) { vm.cargar(periodo) }

    when (val e = estado) {
        is Carga.Listo -> AsistenciasContenido(
            periodo = periodo,
            onPeriodo = { periodo = it },
            trabajados = e.datos.summary.workedDays,
            retardos = e.datos.summary.lates,
            faltas = e.datos.summary.absences,
            dias = e.datos.days.mapNotNull { aVista(it, Fechas.hoy()) },
            barra = barra,
        )
        is Carga.Error -> MensajePantalla(e.mensaje, { vm.cargar(periodo) }, barra)
        Carga.Cargando -> AsistenciasContenido(
            periodo = periodo, onPeriodo = { periodo = it },
            trabajados = 0, retardos = 0, faltas = 0, dias = emptyList(),
            barra = barra, cargando = true,
        )
    }
}
