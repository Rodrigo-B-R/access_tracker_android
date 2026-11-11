package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.ContadorMinimo
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.model.Sala
import com.example.access_tracker.model.SalaRequest
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

@Composable
fun ResMenuScreen(
    onCerrar: () -> Unit,
    onBitacora: () -> Unit,
    onSalas: () -> Unit,
    onExportar: () -> Unit,
    onCuenta: () -> Unit,
    onCerrarSesion: () -> Unit,
    barra: @Composable () -> Unit = {},
) {
    Box(Modifier.fillMaxSize()) {
        // Fondo neutro: el menu es una capa sobre el inicio, sin volver a pedir datos.
        PantallaBase(barra = barra) {}
        Box(Modifier.fillMaxSize().background(TintaBase.copy(alpha = 0.45f)).clickable(onClick = onCerrar))
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 72.dp, end = 24.dp)
                .width(236.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp), ambientColor = TintaBase, spotColor = TintaBase)
                .clip(RoundedCornerShape(24.dp))
                .background(FondoBlanco)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            OpcionMenu(Ico.History, Petroleo100, "Bitácora", "Altas, bajas y traslados", onBitacora)
            OpcionMenu(Ico.Users, Mostaza100, "Salas y mínimos", "Personal mínimo por sala", onSalas)
            OpcionMenu(Ico.Download, Coral100, "Exportar", "CSV de asistencias", onExportar)
            OpcionMenu(Ico.Lock, Petroleo100, "Mi cuenta", "Cambiar contraseña", onCuenta)
            OpcionMenu(Ico.Logout, Rojo100, "Cerrar sesión", "Salir de la cuenta", onCerrarSesion)
        }
    }
}

@Composable
private fun OpcionMenu(icono: Ico, fondo: Color, titulo: String, detalle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconoCuadro(icono, fondo = fondo, lado = 40.dp, radio = 14.dp, tamanoIcono = 24.dp)
        Column(Modifier.weight(1f)) {
            Text(titulo, style = Texto.LabelL, color = TintaBase)
            Text(detalle, style = Texto.BodyS, color = TintaSuave)
        }
    }
}

// ── Salas y minimos ─────────────────────────────────────────────────

class SalasViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<List<Sala>>>(Carga.Cargando)
    val estado: StateFlow<Carga<List<Sala>>> = _estado.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()

    private var originales = emptyMap<Long, Int>()

    fun cargar() {
        viewModelScope.launch {
            val r = ServiceLocator.api.get("/rooms", ListSerializer(Sala.serializer()))
            if (r is ApiResult.Ok) originales = r.value.associate { it.id to it.minStaff }
            _estado.value = r.aCarga()
        }
    }

    /** Guarda los minimos que cambiaron. */
    fun guardar(salas: Map<Long, Int>) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            for ((id, minimo) in salas) {
                if (originales[id] == minimo) continue
                val r = ServiceLocator.api.put("/rooms/$id", SalaRequest(minStaff = minimo), SalaRequest.serializer(), Sala.serializer())
                if (r is ApiResult.Error) {
                    _accion.value = Accion(error = r.error.textoAccion())
                    return@launch
                }
            }
            _accion.value = Accion(listo = true)
        }
    }

    fun agregar(nombre: String) {
        viewModelScope.launch {
            val r = ServiceLocator.api.post("/rooms", SalaRequest(nombre, 1), SalaRequest.serializer(), Sala.serializer())
            if (r is ApiResult.Error) _accion.value = Accion(error = r.error.textoAccion()) else cargar()
        }
    }

    fun consumir() { _accion.value = Accion() }
}

@Composable
fun ResAjustesSalasRoute(onAtras: () -> Unit) {
    val vm: SalasViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val accion by vm.accion.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(accion.listo) { if (accion.listo) { vm.consumir(); onAtras() } }

    val salas = (estado as? Carga.Listo)?.datos
    if (salas == null) {
        com.example.access_tracker.screens.empleada.MensajePantalla(
            (estado as? Carga.Error)?.mensaje ?: "Cargando…",
            if (estado is Carga.Error) ({ vm.cargar() }) else null, {}, suave = estado is Carga.Cargando
        )
        return
    }
    // Minimos editables, por id de sala. Se reinician si cambia la lista cargada.
    val minimos = remember(salas) { mutableStateMapOfMinimos(salas) }
    var agregando by remember { mutableStateOf(false) }
    var nombreNuevo by remember { mutableStateOf("") }

    if (agregando) {
        AlertDialog(
            onDismissRequest = { agregando = false },
            title = { Text("Agregar sala") },
            text = { CampoTexto("Nombre de la sala", nombreNuevo, { nombreNuevo = it }, maxCaracteres = 255) },
            confirmButton = {
                TextButton(
                    enabled = nombreNuevo.isNotBlank(),
                    onClick = { vm.agregar(nombreNuevo.trim()); nombreNuevo = ""; agregando = false },
                ) { Text("Agregar") }
            },
            dismissButton = { TextButton(onClick = { agregando = false }) { Text("Cancelar") } },
        )
    }

    PantallaBase(
        separacion = 14.dp,
        pie = {
            Boton(
                if (accion.enviando) "Guardando…" else "Guardar cambios",
                { vm.guardar(minimos.toMap()) },
                estilo = EstiloBoton.Oscuro,
            )
        },
    ) {
        EncabezadoPantalla("Salas y mínimos", onAtras = onAtras, eyebrow = "Menú")
        Text("Personal mínimo que debe haber en cada sala por turno.", style = Texto.BodyM, color = TintaSuave)
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
        salas.forEach { sala ->
            Tarjeta(radio = 24.dp, relleno = 0.dp) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(sala.name, style = Texto.TitleS, color = TintaBase)
                    ContadorMinimo(
                        Ico.Users,
                        minimos[sala.id] ?: sala.minStaff,
                        { v -> minimos[sala.id] = v.coerceAtLeast(0) },
                        Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, TintaTenue, RoundedCornerShape(24.dp))
                .clickable { agregando = true }
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Plus, tamano = 20.dp)
            Text("Agregar sala", style = Texto.LabelL, color = TintaBase)
        }
    }
}

private fun mutableStateMapOfMinimos(salas: List<Sala>) =
    androidx.compose.runtime.mutableStateMapOf<Long, Int>().apply { salas.forEach { put(it.id, it.minStaff) } }
