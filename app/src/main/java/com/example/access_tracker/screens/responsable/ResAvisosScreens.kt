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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.AvisoItem
import com.example.access_tracker.components.AvisoUrgente
import com.example.access_tracker.components.BarraSegmentos
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.BotonFlotante
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EncabezadoSeccion
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Interruptor
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.Aviso
import com.example.access_tracker.model.AvisoDetalleGestion
import com.example.access_tracker.model.AvisoGestion
import com.example.access_tracker.model.AvisoNuevo
import com.example.access_tracker.model.Persona
import com.example.access_tracker.model.Sala
import com.example.access_tracker.model.Turno
import com.example.access_tracker.screens.empleada.etiquetaDe
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Verde600
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer

// ── Lista ───────────────────────────────────────────────────────────

data class AvisosResponsable(val enviados: List<AvisoGestion>, val recibidos: List<Aviso>)

class ResAvisosViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<AvisosResponsable>>(Carga.Cargando)
    val estado: StateFlow<Carga<AvisosResponsable>> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val yo = ServiceLocator.sesion.leer()?.userId
            val todos = ServiceLocator.api.get("/notices", ListSerializer(AvisoGestion.serializer()))
            val rec = ServiceLocator.api.get("/notices/inbox", ListSerializer(Aviso.serializer()))
            _estado.value = when {
                todos is ApiResult.Error -> Carga.Error(todos.error.texto())
                rec is ApiResult.Error -> Carga.Error(rec.error.texto())
                else -> Carga.Listo(
                    AvisosResponsable(
                        (todos as ApiResult.Ok).value.filter { it.author.id == yo },
                        (rec as ApiResult.Ok).value,
                    )
                )
            }
        }
    }
}

@Composable
fun ResAvisosRoute(
    onAtras: () -> Unit,
    onNuevo: () -> Unit,
    onEstadoLectura: (Long) -> Unit,
    onAbrirRecibido: (Long) -> Unit,
) {
    val vm: ResAvisosViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }
    var filtro by remember { mutableIntStateOf(0) }

    PantallaBase(
        separacion = 16.dp,
        botonFlotante = { BotonFlotante("Nuevo aviso", onNuevo, Modifier.padding(end = 8.dp, bottom = 32.dp)) },
    ) {
        EncabezadoPantalla("Avisos", onAtras = onAtras, grande = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Recibidos", "Enviados").forEachIndexed { i, t ->
                FiltroPill(t, activo = filtro == i, onClick = { filtro = i })
            }
        }
        when (val e = estado) {
            Carga.Cargando -> Text("Cargando…", style = Texto.BodyM, color = TintaSuave)
            is Carga.Error -> Text(e.mensaje, style = Texto.BodyM, color = TintaSuave)
            is Carga.Listo -> {
                val enviados = if (filtro != 1) e.datos.enviados else emptyList()
                val recibidos = if (filtro != 2) e.datos.recibidos else emptyList()
                if (enviados.isEmpty() && recibidos.isEmpty()) {
                    Text("No hay avisos.", style = Texto.BodyM, color = TintaSuave)
                }
                enviados.forEach { a -> AvisoEnviado(a) { onEstadoLectura(a.id) } }
                recibidos.forEach { a ->
                    val (etiqueta, tono) = etiquetaDe(a.noticeType)
                    AvisoItem(
                        etiqueta, tono, "De: ${a.author.name.substringBefore(' ')}", a.subject,
                        sinLeer = !a.read, fondo = FondoBlanco, onClick = { onAbrirRecibido(a.id) },
                    ) {
                        Text(if (a.read) "Leído" else "Sin leer", style = Texto.BodyS, color = TintaSuave)
                    }
                }
            }
        }
    }
}

@Composable
private fun AvisoEnviado(a: AvisoGestion, onClick: () -> Unit) {
    val (etiqueta, tono) = etiquetaDe(a.noticeType)
    val resumen = if (a.requiresConfirmation) "${a.confirmedCount} de ${a.recipientCount} confirmaron"
    else "${a.recipientCount} destinatarios"
    if (a.noticeType == "urgent") {
        AvisoUrgente(titulo = a.subject, hace = "Enviado por ti", onClick = onClick) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (a.requiresConfirmation && a.recipientCount > 0) {
                    BarraSegmentos(a.recipientCount, a.confirmedCount, colorLleno = FondoBlanco, colorVacio = Rojo100.copy(alpha = 0.6f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(resumen, style = Texto.LabelM, color = FondoBlanco)
                    Text("Ver estado", style = Texto.LabelM, color = FondoBlanco)
                }
            }
        }
    } else {
        AvisoItem(etiqueta, tono, "Enviado por ti", a.subject, fondo = FondoBlanco, onClick = onClick) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(Ico.Check, tint = Verde600, tamano = 16.dp)
                Text(resumen, style = Texto.BodyS, color = Verde600)
            }
        }
    }
}

// ── Nuevo aviso ─────────────────────────────────────────────────────

private val TIPOS = listOf("schedule_change" to "Cambio de horario", "meeting" to "Reunión", "reminder" to "Recordatorio", "urgent" to "Urgente")
private val DESTINOS = listOf("all" to "Todo el personal", "shift" to "Un turno", "room" to "Una sala", "people" to "Personas")

data class OpcionesAviso(val turnos: List<Turno>, val salas: List<Sala>, val personal: List<Persona>)

class ResNuevoAvisoViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<OpcionesAviso>>(Carga.Cargando)
    val estado: StateFlow<Carga<OpcionesAviso>> = _estado.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val c = CatalogosCache.obtener()
            val s = ServiceLocator.api.get("/rooms", ListSerializer(Sala.serializer()))
            val p = ServiceLocator.api.get("/staff", ListSerializer(Persona.serializer()))
            _estado.value = when {
                c is ApiResult.Error -> Carga.Error(c.error.texto())
                s is ApiResult.Error -> Carga.Error(s.error.texto())
                p is ApiResult.Error -> Carga.Error(p.error.texto())
                else -> Carga.Listo(OpcionesAviso((c as ApiResult.Ok).value.shifts, (s as ApiResult.Ok).value, (p as ApiResult.Ok).value))
            }
        }
    }

    fun enviar(aviso: AvisoNuevo) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            _accion.value = when (val r = ServiceLocator.api.post("/notices", aviso, AvisoNuevo.serializer(), AvisoGestion.serializer())) {
                is ApiResult.Ok -> Accion(listo = true)
                is ApiResult.Error -> Accion(error = r.error.textoAccion())
            }
        }
    }
}

@Composable
fun ResNuevoAvisoRoute(onCerrar: () -> Unit) {
    val vm: ResNuevoAvisoViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val accion by vm.accion.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(accion.listo) { if (accion.listo) onCerrar() }

    when (val e = estado) {
        is Carga.Listo -> NuevoAvisoContenido(e.datos, accion, onCerrar) { vm.enviar(it) }
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar() }, {})
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, {}, suave = true)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NuevoAvisoContenido(
    o: OpcionesAviso,
    accion: Accion,
    onCerrar: () -> Unit,
    onEnviar: (AvisoNuevo) -> Unit,
) {
    var titulo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableIntStateOf(2) }
    var destino by remember { mutableIntStateOf(0) }
    var confirmar by remember { mutableStateOf(false) }
    var turno by remember { mutableIntStateOf(0) }
    var sala by remember { mutableIntStateOf(0) }
    val personas = remember { mutableStateListOf<Long>() }

    val destinoValido = when (DESTINOS[destino].first) {
        "shift" -> o.turnos.isNotEmpty()
        "room" -> o.salas.isNotEmpty()
        "people" -> personas.isNotEmpty()
        else -> true
    }
    val completo = titulo.isNotBlank() && mensaje.isNotBlank() && destinoValido

    PantallaBase(
        separacion = 16.dp,
        pie = {
            Boton(
                if (accion.enviando) "Enviando…" else "Enviar aviso",
                {
                    val d = DESTINOS[destino].first
                    onEnviar(
                        AvisoNuevo(
                            subject = titulo.trim(), message = mensaje.trim(), noticeType = TIPOS[tipo].first,
                            target = d,
                            shiftId = if (d == "shift") o.turnos[turno].id else null,
                            roomId = if (d == "room") o.salas[sala].id else null,
                            userIds = if (d == "people") personas.toList() else null,
                            requiresConfirmation = confirmar,
                        )
                    )
                },
                habilitado = completo && !accion.enviando,
            )
        },
    ) {
        EncabezadoPantalla("Nuevo aviso", onAtras = onCerrar, cerrar = true)
        CampoTexto("Título", titulo, { titulo = it }, maxCaracteres = 255)
        CampoTexto("Mensaje", mensaje, { mensaje = it }, lineas = 3, maxCaracteres = 255)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tipo", style = Texto.LabelM, color = TintaSuave)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TIPOS.forEachIndexed { i, (_, t) -> ChipOpcion(t, seleccionado = tipo == i, onClick = { tipo = i }) }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Destinatarios", style = Texto.LabelM, color = TintaSuave)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DESTINOS.forEachIndexed { i, (_, t) -> ChipOpcion(t, seleccionado = destino == i, onClick = { destino = i }) }
            }
            when (DESTINOS[destino].first) {
                "shift" -> FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    o.turnos.forEachIndexed { i, t ->
                        ChipOpcion(t.name.replaceFirstChar { it.uppercase() }, seleccionado = turno == i, onClick = { turno = i })
                    }
                }
                "room" -> FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    o.salas.forEachIndexed { i, s -> ChipOpcion(s.name, seleccionado = sala == i, onClick = { sala = i }) }
                }
                "people" -> FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    o.personal.forEach { p ->
                        ChipOpcion(
                            p.name, seleccionado = p.id in personas,
                            onClick = { if (p.id in personas) personas.remove(p.id) else personas.add(p.id) },
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(FondoBlanco)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Pedir confirmación de lectura", style = Texto.LabelL, color = TintaBase)
                Text("Verás quién lo leyó y a qué hora", style = Texto.BodyS, color = TintaSuave)
            }
            Interruptor(confirmar, { confirmar = it })
        }
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
    }
}

// ── Estado de lectura ───────────────────────────────────────────────

class ResEstadoLecturaViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<AvisoDetalleGestion>>(Carga.Cargando)
    val estado: StateFlow<Carga<AvisoDetalleGestion>> = _estado.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            _estado.value = ServiceLocator.api.get("/notices/$id", AvisoDetalleGestion.serializer()).aCarga()
        }
    }
}

@Composable
fun ResEstadoLecturaRoute(id: Long, onAtras: () -> Unit) {
    val vm: ResEstadoLecturaViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    LaunchedEffect(id) { vm.cargar(id) }

    when (val e = estado) {
        is Carga.Listo -> EstadoLecturaContenido(e.datos, onAtras)
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar(id) }, {})
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, {}, suave = true)
    }
}

@Composable
private fun EstadoLecturaContenido(a: AvisoDetalleGestion, onAtras: () -> Unit) {
    val (etiqueta, tono) = etiquetaDe(a.noticeType)
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Estado de lectura", onAtras = onAtras)
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Petroleo700).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Chip(etiqueta, tono = tono)
            Text(a.subject, style = Texto.TitleM, color = FondoCrema)
            if (a.requiresConfirmation) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
                    Text("${a.confirmed.size}", style = Texto.DisplayL, color = Mostaza500)
                    Text("de ${a.recipientCount} confirmaron", style = Texto.LabelL, color = Petroleo100)
                }
                if (a.recipientCount > 0) {
                    BarraSegmentos(a.recipientCount, a.confirmed.size, colorLleno = Mostaza500, colorVacio = Petroleo500)
                }
            } else {
                Text("${a.recipientCount} destinatarios", style = Texto.LabelL, color = Petroleo100)
            }
        }
        EncabezadoSeccion("Sin confirmar · ${a.pending.size}")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            a.pending.forEach { p ->
                val (fondo, tinta) = paleta(p.id)
                FilaPersona(iniciales(p.name), p.name, "", fondoAvatar = fondo, tintaAvatar = tinta, ladoAvatar = 40.dp) {
                    Chip("Sin leer", tono = TonoChip.Peligro)
                }
            }
        }
        EncabezadoSeccion("Confirmaron · ${a.confirmed.size}")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            a.confirmed.forEach { p ->
                val (fondo, tinta) = paleta(p.id)
                FilaPersona(iniciales(p.name), p.name, "", fondoAvatar = fondo, tintaAvatar = tinta, ladoAvatar = 40.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icono(Ico.Check, tint = Verde600, tamano = 16.dp)
                        Text(p.readAt?.let { runCatching { Fechas.horaLocal(it) }.getOrNull() } ?: "", style = Texto.LabelM, color = Verde600)
                    }
                }
            }
        }
    }
}

private fun iniciales(nombre: String) =
    nombre.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }
