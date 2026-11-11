package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EventoBitacora
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.model.MovimientoDetalle
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Verde100
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// La API solo deja ver /audit a Direccion; el responsable ve los movimientos de personal.
private val TIPOS = listOf(null, "hire", "termination", "transfer")
private val FILTROS = listOf("Todo", "Altas", "Bajas", "Traslados")

class ResBitacoraViewModel : ViewModel() {
    var filtro = 0
        private set
    private val _filtroUi = MutableStateFlow(0)
    val filtroUi: StateFlow<Int> = _filtroUi.asStateFlow()
    private val _estado = MutableStateFlow<Carga<List<MovimientoDetalle>>>(Carga.Cargando)
    val estado: StateFlow<Carga<List<MovimientoDetalle>>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _estado.value = Carga.Cargando
            val hoy = Fechas.hoy()
            val tipo = TIPOS[filtro]?.let { "&type=$it" }.orEmpty()
            _estado.value = ServiceLocator.api.get(
                "/movements?startDate=${hoy.minusDays(90)}&endDate=$hoy$tipo",
                ListSerializer(MovimientoDetalle.serializer()),
            ).aCarga()
        }
    }

    fun cambiarFiltro(i: Int) { filtro = i; _filtroUi.value = i; cargar() }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResBitacoraRoute(onAtras: () -> Unit) {
    val vm: ResBitacoraViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val filtro by vm.filtroUi.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Bitácora", onAtras = onAtras, eyebrow = "Movimientos de personal · 90 días")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FILTROS.forEachIndexed { i, t -> FiltroPill(t, activo = filtro == i, onClick = { vm.cambiarFiltro(i) }) }
        }
        when (val e = estado) {
            Carga.Cargando -> Text("Cargando…", style = Texto.BodyM, color = TintaSuave)
            is Carga.Error -> Text(e.mensaje, style = Texto.BodyM, color = TintaSuave)
            is Carga.Listo -> {
                if (e.datos.isEmpty()) Text("Sin movimientos en este periodo.", style = Texto.BodyM, color = TintaSuave)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    e.datos.forEach { m ->
                        val fecha = runCatching {
                            DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-MX"))
                                .format(LocalDate.parse(m.date)).replace(".", "")
                        }.getOrDefault(m.date)
                        val detalle = "Registrado por ${m.doneBy.name} · $fecha"
                        when (m.type) {
                            "hire" -> EventoBitacora(Ico.Userplus, Verde100, "Alta", TonoChip.Exito, m.user.name, detalle)
                            "termination" -> EventoBitacora(
                                Ico.Userminus, Coral100, "Baja", TonoChip.Coral, m.user.name, detalle,
                                motivo = m.terminationReason?.let { "Motivo: ${it.name}" },
                            )
                            else -> EventoBitacora(
                                Ico.Swap, Mostaza100, "Traslado", TonoChip.Alerta,
                                "${m.user.name}: ${m.originDaycare?.name ?: "—"} → ${m.destinationDaycare?.name ?: "—"}", detalle,
                            )
                        }
                    }
                }
            }
        }
    }
}
