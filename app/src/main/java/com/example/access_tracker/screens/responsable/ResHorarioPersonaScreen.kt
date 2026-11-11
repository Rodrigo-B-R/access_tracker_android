package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.DiaHorarioRequest
import com.example.access_tracker.model.Horario
import com.example.access_tracker.model.HorarioDePersona
import com.example.access_tracker.model.HorarioRequest
import com.example.access_tracker.model.Sala
import com.example.access_tracker.model.Turno
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

private val Dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val Iconos = listOf(Ico.Sun, Ico.Moon, Ico.Clock, Ico.Clock)

data class EditorHorario(val persona: HorarioDePersona, val turnos: List<Turno>, val salas: List<Sala>)

class ResHorarioPersonaViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<EditorHorario>>(Carga.Cargando)
    val estado: StateFlow<Carga<EditorHorario>> = _estado.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            val h = ServiceLocator.api.get("/schedules/$id", HorarioDePersona.serializer())
            val s = ServiceLocator.api.get("/rooms", ListSerializer(Sala.serializer()))
            val c = CatalogosCache.obtener()
            _estado.value = when {
                h is ApiResult.Error -> Carga.Error(h.error.texto())
                s is ApiResult.Error -> Carga.Error(s.error.texto())
                c is ApiResult.Error -> Carga.Error(c.error.texto())
                else -> Carga.Listo(
                    EditorHorario((h as ApiResult.Ok).value, (c as ApiResult.Ok).value.shifts, (s as ApiResult.Ok).value)
                )
            }
        }
    }

    fun guardar(id: Long, dias: List<DiaHorarioRequest>) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            _accion.value = when (
                val r = ServiceLocator.api.put("/schedules/$id", HorarioRequest(days = dias), HorarioRequest.serializer(), Horario.serializer())
            ) {
                is ApiResult.Ok -> Accion(listo = true)
                is ApiResult.Error -> Accion(error = r.error.textoAccion())
            }
        }
    }
}

@Composable
fun ResHorarioPersonaRoute(id: Long, onAtras: () -> Unit) {
    val vm: ResHorarioPersonaViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val accion by vm.accion.collectAsState()
    LaunchedEffect(id) { vm.cargar(id) }
    LaunchedEffect(accion.listo) { if (accion.listo) onAtras() }

    when (val e = estado) {
        is Carga.Listo -> EditorHorarioContenido(e.datos, accion, onAtras) { vm.guardar(id, it) }
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar(id) }, {})
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, {}, suave = true)
    }
}

@Composable
private fun EditorHorarioContenido(
    datos: EditorHorario,
    accion: Accion,
    onAtras: () -> Unit,
    onGuardar: (List<DiaHorarioRequest>) -> Unit,
) {
    val p = datos.persona.person
    val actual = datos.persona.schedule?.days.orEmpty()
    val descanso = datos.turnos.size           // ultima opcion = Descanso
    val opciones = datos.turnos.map { it.name.replaceFirstChar { c -> c.uppercase() } } + "Descanso"
    // Un turno por dia: indice en el catalogo, o [descanso].
    val turnos = remember {
        mutableStateListOf<Int>().apply {
            DIAS_API.forEach { d ->
                val dia = actual.firstOrNull { it.weekday == d }
                add(dia?.let { x -> datos.turnos.indexOfFirst { it.id == x.shiftId }.takeIf { it >= 0 } } ?: descanso)
            }
        }
    }
    val salaActual = actual.firstOrNull()?.roomId
    var sala by remember { mutableIntStateOf(datos.salas.indexOfFirst { it.id == salaActual }.coerceAtLeast(0)) }
    var salaCambiada by remember { mutableIntStateOf(0) }
    val diasQueVa = turnos.count { it != descanso }

    PantallaBase(
        separacion = 14.dp,
        pie = {
            Boton(
                if (accion.enviando) "Guardando…" else "Guardar horario",
                {
                    val porDefecto = datos.salas.getOrNull(sala)?.id
                    val dias = DIAS_API.mapIndexedNotNull { i, d ->
                        val t = datos.turnos.getOrNull(turnos[i]) ?: return@mapIndexedNotNull null
                        // Si no se cambio la sala, cada dia conserva la suya.
                        val salaDia = if (salaCambiada == 0) actual.firstOrNull { it.weekday == d }?.roomId else null
                        val roomId = salaDia ?: porDefecto ?: return@mapIndexedNotNull null
                        DiaHorarioRequest(d, t.id, roomId)
                    }
                    onGuardar(dias)
                },
                habilitado = datos.salas.isNotEmpty() && !accion.enviando,
            )
        },
    ) {
        EncabezadoPantalla("Horario de la persona", onAtras = onAtras, eyebrow = "Horarios")
        Tarjeta(radio = 24.dp) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                val (fondo, tinta) = paleta(p.id)
                Avatar(p.name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() },
                    fondo = fondo, tinta = tinta, lado = 48.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(p.name, style = Texto.TitleS, color = TintaBase)
                    Text(p.position?.name.orEmpty(), style = Texto.BodyS, color = TintaSuave)
                }
                Chip("$diasQueVa días", tono = TonoChip.Info, punto = false)
            }
        }
        if (datos.salas.isNotEmpty()) {
            CampoOpciones(
                "Sala", datos.salas[sala].name, datos.salas.map { it.name },
                onElegido = { sala = it; salaCambiada = 1 },
            )
        }
        Text("Turno por día", style = Texto.LabelM, color = TintaSuave)
        Dias.forEachIndexed { i, dia ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(dia, style = Texto.LabelL, color = TintaBase)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    opciones.forEachIndexed { t, nombre ->
                        ChipOpcion(
                            nombre,
                            Modifier.weight(1f),
                            seleccionado = turnos[i] == t,
                            icono = if (t == descanso) Ico.Coffee else Iconos.getOrElse(t) { Ico.Clock },
                            onClick = { turnos[i] = t }
                        )
                    }
                }
            }
        }
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
        Nota(
            "El horario nuevo aplica desde hoy y sustituye al anterior. Sirve para saber quién debería estar cada día.",
            icono = Ico.Calendar
        )
    }
}
