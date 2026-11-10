package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.model.Horario
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.nullable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HorarioViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<Horario?>>(Carga.Cargando)
    val estado: StateFlow<Carga<Horario?>> = _estado.asStateFlow()

    fun cargar() {
        _estado.value = Carga.Cargando
        viewModelScope.launch {
            // /schedules/me devuelve null si la persona no tiene horario.
            _estado.value = ServiceLocator.api.get("/schedules/me", Horario.serializer().nullable).aCarga()
        }
    }
}

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val LETRAS = listOf("L", "M", "M", "J", "V", "S", "D")
private val MESES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
)

/** Dias de la semana de [hoy] (lun-vie; sab/dom solo si hay turno) con su turno del horario. */
internal fun diasDeLaSemana(horario: Horario?, hoy: LocalDate): List<DiaVista> {
    val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return (0..6).mapNotNull { i ->
        val fecha = lunes.plusDays(i.toLong())
        val turno = horario?.days?.firstOrNull { it.weekday == fecha.dayOfWeek.name.lowercase() }
        if (i >= 5 && turno == null) return@mapNotNull null
        DiaVista(
            letra = LETRAS[i],
            numero = fecha.dayOfMonth.toString(),
            titulo = "${DIAS[i]} ${fecha.dayOfMonth}",
            horas = if (turno != null) {
                "${Fechas.horaCorta(turno.shift.startTime)} – ${Fechas.horaCorta(turno.shift.endTime)}"
            } else "Descanso",
            sala = turno?.let { "Sala ${it.room.name}" },
            estado = when {
                turno == null -> EstadoDia.Descanso
                fecha == hoy -> EstadoDia.Hoy
                else -> EstadoDia.Normal
            },
        )
    }
}

internal fun textoSemana(hoy: LocalDate): String {
    val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val viernes = lunes.plusDays(4)
    return if (lunes.month == viernes.month) {
        "Semana del ${lunes.dayOfMonth} al ${viernes.dayOfMonth} de ${MESES[viernes.monthValue - 1]}"
    } else {
        "Semana del ${lunes.dayOfMonth} de ${MESES[lunes.monthValue - 1]} " +
            "al ${viernes.dayOfMonth} de ${MESES[viernes.monthValue - 1]}"
    }
}

@Composable
fun HorarioRoute(barra: @Composable () -> Unit) {
    val vm: HorarioViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    when (val e = estado) {
        is Carga.Listo -> {
            val hoy = Fechas.hoy()
            if (e.datos == null) {
                MensajePantalla("Todavía no tienes un horario asignado. Pídelo a tu responsable.", null, barra)
            } else {
                HorarioContenido(textoSemana(hoy), diasDeLaSemana(e.datos, hoy), barra)
            }
        }
        is Carga.Error -> MensajePantalla(e.mensaje, { vm.cargar() }, barra)
        Carga.Cargando -> MensajePantalla("Cargando…", null, barra, suave = true)
    }
}

/** Pantalla con un mensaje centrado y, opcionalmente, boton de reintento. */
@Composable
internal fun MensajePantalla(
    texto: String,
    onReintentar: (() -> Unit)?,
    barra: @Composable () -> Unit,
    suave: Boolean = false,
) {
    PantallaBase(barra = barra) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                texto,
                style = Texto.TitleM,
                color = if (suave) TintaSuave else TintaBase,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            if (onReintentar != null) Boton("Reintentar", onClick = onReintentar, estilo = EstiloBoton.Oscuro)
        }
    }
}
