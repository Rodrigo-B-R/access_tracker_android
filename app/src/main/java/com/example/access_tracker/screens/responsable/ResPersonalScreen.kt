package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.BarraBusqueda
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.BotonFlotanteIcono
import com.example.access_tracker.components.EncabezadoSeccion
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.Flecha
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.Persona
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaSuave
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

/** Activos y bajas de la estancia (el responsable no ve a direccion; la API ya lo filtra). */
data class Plantilla(val activos: List<Persona>, val bajas: List<Persona>)

class ResPersonalViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<Plantilla>>(Carga.Cargando)
    val estado: StateFlow<Carga<Plantilla>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val ser = ListSerializer(Persona.serializer())
            val a = ServiceLocator.api.get("/staff?active=true", ser)
            val b = ServiceLocator.api.get("/staff?active=false", ser)
            _estado.value = when {
                a is ApiResult.Error -> Carga.Error(a.error.texto())
                b is ApiResult.Error -> Carga.Error(b.error.texto())
                else -> Carga.Listo(Plantilla((a as ApiResult.Ok).value, (b as ApiResult.Ok).value))
            }
        }
    }
}

@Composable
fun ResPersonalRoute(
    onAvisos: () -> Unit,
    onPersona: (Long) -> Unit,
    onAlta: () -> Unit,
    barra: @Composable () -> Unit = {},
) {
    val vm: ResPersonalViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val sinLeer = rememberAvisosSinLeer()
    LaunchedEffect(Unit) { vm.cargar() }

    when (val e = estado) {
        is Carga.Listo -> ResPersonalContenido(e.datos, sinLeer, onAvisos, onPersona, onAlta, barra)
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, barra)
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, barra, suave = true)
    }
}

@Composable
fun ResPersonalContenido(
    plantilla: Plantilla,
    avisosSinLeer: Int,
    onAvisos: () -> Unit,
    onPersona: (Long) -> Unit,
    onAlta: () -> Unit,
    barra: @Composable () -> Unit = {}
) {
    var vista by remember { mutableIntStateOf(0) }
    var busqueda by remember { mutableStateOf("") }
    val lista = (if (vista == 0) plantilla.activos else plantilla.bajas)
        .filter { busqueda.isBlank() || it.name.contains(busqueda.trim(), ignoreCase = true) }
    val grupos = lista.groupBy { it.position?.name ?: "Sin puesto" }.toSortedMap()
    PantallaBase(
        barra = barra,
        separacion = 16.dp,
        botonFlotante = { BotonFlotanteIcono(Ico.Userplus, Mostaza500, onAlta, Modifier.padding(end = 8.dp)) },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                TituloPantalla("Personal", subtitulo = "${plantilla.activos.size} personas activas")
            }
            BotonCampana(avisosSinLeer, onAvisos)
        }
        BarraBusqueda("Buscar por nombre", valor = busqueda, onCambio = { busqueda = it })
        Segmentado(
            listOf("Activos (${plantilla.activos.size})", "Bajas (${plantilla.bajas.size})"),
            vista, { vista = it }
        )
        if (lista.isEmpty()) Text("No hay personas que mostrar.", style = Texto.BodyM, color = TintaSuave)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            grupos.forEach { (puesto, personas) ->
                EncabezadoSeccion("$puesto · ${personas.size}")
                personas.forEach { p ->
                    val (fondo, tinta) = paleta(p.id)
                    FilaPersona(
                        p.iniciales, p.name, p.username,
                        fondoAvatar = fondo, tintaAvatar = tinta, ladoAvatar = 40.dp,
                        onClick = { onPersona(p.id) },
                    ) { Flecha() }
                }
            }
        }
    }
}
