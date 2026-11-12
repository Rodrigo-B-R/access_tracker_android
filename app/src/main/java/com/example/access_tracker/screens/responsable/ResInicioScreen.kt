package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.access_tracker.components.BotonCircular
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.SalaMini
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.model.Cobertura
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Verde600
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResInicioViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<Cobertura>>(Carga.Cargando)
    val estado: StateFlow<Carga<Cobertura>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _estado.value = ServiceLocator.api.get("/coverage", Cobertura.serializer()).aCarga()
        }
    }
}

/** "Missing 1 in Maternal A (matutino)" -> "Falta 1 en Maternal A · matutino". */
internal fun alertaEnEspanol(alerta: String): String {
    val m = Regex("""Missing (\d+) in (.+?) \((.+)\)""").matchEntire(alerta) ?: return alerta
    val n = m.groupValues[1].toInt()
    return (if (n == 1) "Falta 1 en " else "Faltan $n en ") + m.groupValues[2] + " · " + m.groupValues[3]
}

@Composable
fun ResInicioRoute(
    onAvisos: () -> Unit,
    onMenu: () -> Unit,
    onAtender: () -> Unit,
    onVerAsistencia: () -> Unit,
    barra: @Composable () -> Unit = {},
) {
    val vm: ResInicioViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val sinLeer = rememberAvisosSinLeer()
    LaunchedEffect(Unit) { vm.cargar() }

    when (val e = estado) {
        is Carga.Listo -> ResInicioContenido(e.datos, sinLeer, onAvisos, onMenu, onAtender, onVerAsistencia, barra)
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, barra)
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, barra, suave = true)
    }
}

@Composable
fun ResInicioContenido(
    c: Cobertura,
    avisosSinLeer: Int,
    onAvisos: () -> Unit,
    onMenu: () -> Unit,
    onAtender: () -> Unit,
    onVerAsistencia: () -> Unit,
    barra: @Composable () -> Unit = {},
) {
    // Por sala, el turno que ya empezo (o el primero) es el que cuenta para la tarjeta.
    val salas = c.rooms.map { r -> r to (r.shifts.firstOrNull { it.started } ?: r.shifts.firstOrNull()) }
    val bajas = salas.filter { it.second?.belowMinimum == true }
    PantallaBase(barra = barra, separacion = 20.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Estancia ${c.daycare.name}", style = Texto.TitleM, color = TintaBase)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icono(Ico.Cloud, tint = Verde600, tamano = 16.dp)
                    Text("Actualizado ahora", style = Texto.LabelM, color = Verde600)
                }
            }
            BotonCampana(contador = avisosSinLeer, onClick = onAvisos)
            BotonCircular(Ico.More, onMenu)
        }
        bajas.firstOrNull()?.let { (sala, turno) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Rojo600)
                    .padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icono(Ico.Alert, tint = FondoBlanco, tamano = 28.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${sala.room.name} bajo el mínimo", style = Texto.TitleS, color = FondoBlanco)
                    Text(
                        "${turno?.present ?: 0} de ${sala.minimum} presentes hoy",
                        style = Texto.BodyM, color = FondoBlanco
                    )
                }
                Text(
                    "Atender", style = Texto.LabelM, color = TintaBase,
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(FondoBlanco)
                        .clickable(onClick = onAtender)
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                )
            }
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(28.dp)).background(Petroleo700).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Presentes hoy", style = Texto.LabelM, color = FondoCrema)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                    Text("${c.present}", style = Texto.DisplayL, color = FondoCrema)
                    Text("/${c.scheduled}", style = Texto.TitleM, color = Petroleo100)
                }
                Text("${c.pending} sin checar", style = Texto.BodyS, color = Petroleo100)
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Eyebrow("Cobertura por sala")
            Text(
                "Ver asistencia",
                style = Texto.LabelM,
                color = Petroleo700,
                modifier = Modifier.clickable(onClick = onVerAsistencia)
            )
        }
        if (salas.isEmpty()) {
            Text("Esta estancia aún no tiene salas.", style = Texto.BodyM, color = TintaSuave)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            salas.chunked(2).forEach { par ->
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    par.forEach { (sala, turno) ->
                        SalaMini(
                            sala.room.name,
                            turno?.present ?: 0,
                            maxOf(sala.minimum, turno?.scheduled ?: 0),
                            Modifier.weight(1f),
                            bajoMinimo = turno?.belowMinimum == true,
                        )
                    }
                    if (par.size == 1) Column(Modifier.weight(1f)) {}
                }
            }
        }
        c.alerts.forEach { alerta ->
            Tarjeta(modifier = Modifier.clickable { onAtender() }, radio = 24.dp) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconoCuadro(Ico.Flag, fondo = Coral100, lado = 44.dp, radio = 14.dp, tamanoIcono = 24.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Turno sin cubrir", style = Texto.LabelL, color = TintaBase)
                        Text(alertaEnEspanol(alerta), style = Texto.BodyS, color = TintaSuave)
                    }
                }
            }
        }
    }
}
