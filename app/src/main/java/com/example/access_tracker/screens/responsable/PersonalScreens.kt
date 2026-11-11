package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.BotonCircular
import com.example.access_tracker.components.CampoFecha
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TarjetaCifra
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.AltaPersonaRequest
import com.example.access_tracker.model.AsistenciaPersona
import com.example.access_tracker.model.BajaRequest
import com.example.access_tracker.model.Catalogos
import com.example.access_tracker.model.EdicionPersonaRequest
import com.example.access_tracker.model.Estancia
import com.example.access_tracker.model.PasswordTemporal
import com.example.access_tracker.model.Persona
import com.example.access_tracker.model.PersonaCreada
import com.example.access_tracker.model.PersonaDetalle
import com.example.access_tracker.model.TrasladoRequest
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Verde100
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.time.LocalDate

// ── Alta y edicion ──────────────────────────────────────────────────

class AltaPersonalViewModel : ViewModel() {
    private val _catalogos = MutableStateFlow<Carga<Catalogos>>(Carga.Cargando)
    val catalogos: StateFlow<Carga<Catalogos>> = _catalogos.asStateFlow()
    private val _persona = MutableStateFlow<PersonaDetalle?>(null)
    val persona: StateFlow<PersonaDetalle?> = _persona.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()
    private val _creada = MutableStateFlow<PersonaCreada?>(null)
    val creada: StateFlow<PersonaCreada?> = _creada.asStateFlow()

    fun cargar(id: Long?) {
        viewModelScope.launch {
            val c = CatalogosCache.obtener()
            if (id != null) {
                val p = ServiceLocator.api.get("/staff/$id", PersonaDetalle.serializer())
                if (p is ApiResult.Ok) _persona.value = p.value
            }
            _catalogos.value = when (c) {
                is ApiResult.Ok -> Carga.Listo(c.value)
                is ApiResult.Error -> Carga.Error(c.error.texto())
            }
        }
    }

    fun guardar(id: Long?, nombre: String, clave: String, correo: String, telefono: String, puestoId: Long) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            if (id == null) {
                val body = AltaPersonaRequest(nombre.trim(), clave.trim(), correo.trim(), telefono.trim(), puestoId)
                when (val r = ServiceLocator.api.post("/staff", body, AltaPersonaRequest.serializer(), PersonaCreada.serializer())) {
                    is ApiResult.Ok -> { _creada.value = r.value; _accion.value = Accion() }
                    is ApiResult.Error -> _accion.value = Accion(error = r.error.textoAccion())
                }
            } else {
                val body = EdicionPersonaRequest(nombre.trim(), clave.trim(), correo.trim(), telefono.trim(), puestoId)
                when (val r = ServiceLocator.api.put("/staff/$id", body, EdicionPersonaRequest.serializer(), Persona.serializer())) {
                    is ApiResult.Ok -> _accion.value = Accion(listo = true)
                    is ApiResult.Error -> _accion.value = Accion(error = r.error.textoAccion())
                }
            }
        }
    }
}

@Composable
fun AltaPersonalRoute(id: Long?, onCerrar: () -> Unit) {
    val vm: AltaPersonalViewModel = viewModel()
    val cat by vm.catalogos.collectAsState()
    val persona by vm.persona.collectAsState()
    val accion by vm.accion.collectAsState()
    val creada by vm.creada.collectAsState()
    LaunchedEffect(id) { vm.cargar(id) }
    LaunchedEffect(accion.listo) { if (accion.listo) onCerrar() }

    creada?.let { DialogoPasswordTemporal(it.name, it.temporaryPassword, onCerrar) }

    val catalogos = (cat as? Carga.Listo)?.datos
    if (catalogos == null || (id != null && persona == null)) {
        com.example.access_tracker.screens.empleada.MensajePantalla(
            (cat as? Carga.Error)?.mensaje ?: "Cargando…",
            if (cat is Carga.Error) ({ vm.cargar(id) }) else null, {}, suave = cat is Carga.Cargando
        )
        return
    }
    val p = persona
    var nombre by remember { mutableStateOf(p?.name.orEmpty()) }
    var clave by remember { mutableStateOf(p?.username.orEmpty()) }
    var correo by remember { mutableStateOf(p?.email.orEmpty()) }
    var telefono by remember { mutableStateOf(p?.phone.orEmpty()) }
    var puesto by remember {
        mutableIntStateOf(catalogos.positions.indexOfFirst { it.id == p?.position?.id }.coerceAtLeast(0))
    }
    val completo = nombre.isNotBlank() && clave.isNotBlank() && correo.contains('@') &&
        telefono.isNotBlank() && catalogos.positions.isNotEmpty()

    PantallaBase(
        pie = {
            Boton(
                if (accion.enviando) "Guardando…" else if (id == null) "Guardar y crear cuenta" else "Guardar cambios",
                {
                    vm.guardar(id, nombre, clave, correo, telefono, catalogos.positions[puesto].id)
                },
                estilo = EstiloBoton.Oscuro,
                habilitado = completo && !accion.enviando,
            )
        },
    ) {
        EncabezadoPantalla(
            if (id == null) "Alta de personal" else "Editar datos", onAtras = onCerrar, cerrar = true, eyebrow = "Personal"
        )
        CampoTexto("Nombre completo", nombre, { nombre = it }, maxCaracteres = 255)
        CampoOpciones(
            "Puesto",
            catalogos.positions.getOrNull(puesto)?.name ?: "—",
            catalogos.positions.map { it.name },
            onElegido = { puesto = it },
        )
        CampoTexto("Clave de acceso", clave, { clave = it }, icono = Ico.Idcard, maxCaracteres = 255, sinAutocorreccion = true)
        CampoTexto("Correo", correo, { correo = it }, maxCaracteres = 255, tipoTeclado = KeyboardType.Email, sinAutocorreccion = true)
        CampoTexto("Teléfono", telefono, { telefono = it }, maxCaracteres = 255, tipoTeclado = KeyboardType.Phone)
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
        if (id == null) {
            Nota(
                "Se crea la cuenta con una contraseña temporal que verás una sola vez. Deberá cambiarla en su primer ingreso.",
                icono = Ico.Lock, fondo = Mostaza100, tint = TintaBase,
            )
        }
    }
}

// ── Perfil ──────────────────────────────────────────────────────────

class PerfilPersonaViewModel : ViewModel() {
    private val _estado = MutableStateFlow<Carga<PersonaDetalle>>(Carga.Cargando)
    val estado: StateFlow<Carga<PersonaDetalle>> = _estado.asStateFlow()
    private val _resumen = MutableStateFlow<AsistenciaPersona?>(null)
    val resumen: StateFlow<AsistenciaPersona?> = _resumen.asStateFlow()
    private val _clave = MutableStateFlow<String?>(null)
    val clave: StateFlow<String?> = _clave.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            val r = ServiceLocator.api.get("/staff/$id", PersonaDetalle.serializer())
            _estado.value = when (r) {
                is ApiResult.Ok -> Carga.Listo(r.value)
                is ApiResult.Error -> Carga.Error(r.error.texto())
            }
            val hoy = Fechas.hoy()
            val a = ServiceLocator.api.get(
                "/attendance/$id?startDate=${hoy.withDayOfMonth(1)}&endDate=$hoy", AsistenciaPersona.serializer()
            )
            if (a is ApiResult.Ok) _resumen.value = a.value
        }
    }

    fun restablecerPassword(id: Long) {
        viewModelScope.launch {
            when (val r = ServiceLocator.api.post("/staff/$id/password", "{}", PasswordTemporal.serializer())) {
                is ApiResult.Ok -> _clave.value = r.value.temporaryPassword
                is ApiResult.Error -> _error.value = r.error.textoAccion()
            }
        }
    }

    fun cerrarClave() { _clave.value = null }
}

@Composable
fun PerfilPersonaRoute(
    id: Long,
    onAtras: () -> Unit,
    onEditar: () -> Unit,
    onBaja: () -> Unit,
    onHorario: () -> Unit,
) {
    val vm: PerfilPersonaViewModel = viewModel()
    val estado by vm.estado.collectAsState()
    val resumen by vm.resumen.collectAsState()
    val clave by vm.clave.collectAsState()
    val error by vm.error.collectAsState()
    LaunchedEffect(id) { vm.cargar(id) }

    when (val e = estado) {
        is Carga.Listo -> {
            clave?.let { DialogoPasswordTemporal(e.datos.name, it) { vm.cerrarClave() } }
            PerfilPersonaContenido(
                e.datos, resumen, error, onAtras, onEditar, onBaja, onHorario,
                onPassword = { vm.restablecerPassword(id) },
            )
        }
        is Carga.Error -> com.example.access_tracker.screens.empleada.MensajePantalla(e.mensaje, { vm.cargar(id) }, {})
        Carga.Cargando -> com.example.access_tracker.screens.empleada.MensajePantalla("Cargando…", null, {}, suave = true)
    }
}

private fun fechaCorta(iso: String): String = runCatching {
    java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.forLanguageTag("es-MX"))
        .format(LocalDate.parse(iso)).replace(".", "")
}.getOrDefault(iso)

@Composable
fun PerfilPersonaContenido(
    p: PersonaDetalle,
    resumen: AsistenciaPersona?,
    error: String?,
    onAtras: () -> Unit,
    onEditar: () -> Unit,
    onBaja: () -> Unit,
    onHorario: () -> Unit,
    onPassword: () -> Unit,
) {
    val (fondo, tinta) = paleta(p.id)
    val dias = p.currentSchedule?.days.orEmpty()
    val turnos = dias.map { it.shift.name }.distinct().joinToString(" / ").ifEmpty { "Sin horario" }
    val salas = dias.map { it.room.name }.distinct().joinToString(", ")
    val alta = p.movements.firstOrNull { it.type == "hire" }?.date
    PantallaBase(
        separacion = 16.dp,
        pie = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (p.isActive) {
                    Boton("Editar horario", onHorario, estilo = EstiloBoton.Oscuro)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Boton("Editar datos", onEditar, Modifier.weight(1f), EstiloBoton.Secundario)
                        Boton("Baja o traslado", onBaja, Modifier.weight(1f), EstiloBoton.Peligro)
                    }
                    Boton("Restablecer contraseña", onPassword, estilo = EstiloBoton.Secundario)
                }
            }
        },
    ) {
        EncabezadoPantalla(
            "Perfil", onAtras = onAtras,
            accion = if (p.isActive) ({ BotonCircular(Ico.Edit, onEditar) }) else null
        )
        error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp)).background(Petroleo700).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Avatar(p.name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() },
                fondo = fondo, tinta = tinta, lado = 64.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(p.name, style = Texto.TitleL, color = FondoCrema)
                Text(
                    listOfNotNull(salas.ifEmpty { null }, p.position?.name).joinToString(" · "),
                    style = Texto.BodyM, color = Petroleo100
                )
            }
            if (p.isActive) Chip("Activa", tono = TonoChip.Exito) else Chip("De baja", tono = TonoChip.Peligro)
        }
        Tarjeta(radio = 24.dp, relleno = 0.dp) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)) {
                DatoPerfil(Ico.Clock, "Turno", turnos)
                DatoPerfil(Ico.Idcard, "Clave", p.username)
                DatoPerfil(Ico.Calendar, "Alta", alta?.let(::fechaCorta) ?: "—")
            }
        }
        Eyebrow("Asistencias · este mes")
        val s = resumen?.summary
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaCifra("${s?.workedDays ?: "–"}", "Presente", Verde100, Modifier.weight(1f))
            TarjetaCifra("${s?.lates ?: "–"}", "Retardos", Mostaza100, Modifier.weight(1f))
            TarjetaCifra("${s?.absences ?: "–"}", "Faltas", Rojo100, Modifier.weight(1f))
        }
        Eyebrow("Historial")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            p.movements.sortedByDescending { it.date }.forEach { m ->
                val titulo = when (m.type) {
                    "hire" -> "Alta en la plantilla"
                    "termination" -> "Baja" + (m.terminationReason?.let { ": ${it.name}" } ?: "")
                    else -> "Traslado de estancia"
                }
                EventoHistorial(titulo, fechaCorta(m.date))
            }
        }
    }
}

@Composable
private fun DatoPerfil(icono: Ico, etiqueta: String, valor: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icono(icono, tint = TintaSuave, tamano = 20.dp)
        Text(etiqueta, style = Texto.BodyM, color = TintaSuave, modifier = Modifier.weight(1f))
        Text(valor, style = Texto.LabelM, color = TintaBase)
    }
}

@Composable
private fun EventoHistorial(titulo: String, fecha: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(Petroleo700))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = Texto.LabelM, color = TintaBase)
            Text(fecha, style = Texto.BodyS, color = TintaSuave)
        }
    }
}

// ── Baja y traslado ─────────────────────────────────────────────────

class DarDeBajaViewModel : ViewModel() {
    private val _persona = MutableStateFlow<Carga<PersonaDetalle>>(Carga.Cargando)
    val persona: StateFlow<Carga<PersonaDetalle>> = _persona.asStateFlow()
    private val _catalogos = MutableStateFlow<Catalogos?>(null)
    val catalogos: StateFlow<Catalogos?> = _catalogos.asStateFlow()
    private val _estancias = MutableStateFlow<List<Estancia>>(emptyList())
    val estancias: StateFlow<List<Estancia>> = _estancias.asStateFlow()
    private val _accion = MutableStateFlow(Accion())
    val accion: StateFlow<Accion> = _accion.asStateFlow()

    fun cargar(id: Long) {
        viewModelScope.launch {
            val p = ServiceLocator.api.get("/staff/$id", PersonaDetalle.serializer())
            _persona.value = when (p) {
                is ApiResult.Ok -> Carga.Listo(p.value)
                is ApiResult.Error -> Carga.Error(p.error.texto())
            }
            (CatalogosCache.obtener() as? ApiResult.Ok)?.let { _catalogos.value = it.value }
            val e = ServiceLocator.api.get("/daycares", ListSerializer(Estancia.serializer()))
            if (e is ApiResult.Ok) _estancias.value = e.value
        }
    }

    /** La API usa la fecha de hoy si no se manda otra. */
    fun confirmarBaja(id: Long, motivoId: Long) {
        enviar {
            ServiceLocator.api.post(
                "/staff/$id/terminate", BajaRequest(motivoId), BajaRequest.serializer(), Persona.serializer()
            )
        }
    }

    fun confirmarTraslado(id: Long, estanciaId: Long) {
        enviar {
            ServiceLocator.api.post(
                "/staff/$id/transfer", TrasladoRequest(estanciaId), TrasladoRequest.serializer(), Persona.serializer()
            )
        }
    }

    private fun <T> enviar(llamada: suspend () -> ApiResult<T>) {
        if (_accion.value.enviando) return
        _accion.value = Accion(enviando = true)
        viewModelScope.launch {
            _accion.value = when (val r = llamada()) {
                is ApiResult.Ok -> Accion(listo = true)
                is ApiResult.Error -> Accion(error = r.error.textoAccion())
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DarDeBajaRoute(id: Long, onCerrar: () -> Unit, onTerminado: () -> Unit) {
    val vm: DarDeBajaViewModel = viewModel()
    val persona by vm.persona.collectAsState()
    val catalogos by vm.catalogos.collectAsState()
    val estancias by vm.estancias.collectAsState()
    val accion by vm.accion.collectAsState()
    LaunchedEffect(id) { vm.cargar(id) }
    LaunchedEffect(accion.listo) { if (accion.listo) onTerminado() }

    val p = (persona as? Carga.Listo)?.datos
    if (p == null || catalogos == null) {
        com.example.access_tracker.screens.empleada.MensajePantalla(
            (persona as? Carga.Error)?.mensaje ?: "Cargando…",
            if (persona is Carga.Error) ({ vm.cargar(id) }) else null, {}, suave = persona is Carga.Cargando
        )
        return
    }
    val motivos = catalogos!!.terminationReasons
    val otras = estancias.filter { it.id != p.daycare?.id }
    var modo by remember { mutableIntStateOf(0) }
    var motivo by remember { mutableIntStateOf(0) }
    var destino by remember { mutableIntStateOf(0) }
    val puedeTrasladar = otras.isNotEmpty()
    val (fondo, tinta) = paleta(p.id)

    PantallaBase(
        pie = {
            if (modo == 0) {
                Boton(
                    if (accion.enviando) "Enviando…" else "Confirmar baja",
                    { motivos.getOrNull(motivo)?.let { vm.confirmarBaja(id, it.id) } },
                    estilo = EstiloBoton.Peligro,
                    habilitado = motivos.isNotEmpty() && !accion.enviando,
                )
            } else {
                Boton(
                    if (accion.enviando) "Enviando…" else "Confirmar traslado",
                    { otras.getOrNull(destino)?.let { vm.confirmarTraslado(id, it.id) } },
                    estilo = EstiloBoton.Oscuro,
                    habilitado = puedeTrasladar && !accion.enviando,
                )
            }
        },
    ) {
        EncabezadoPantalla(if (modo == 0) "Dar de baja" else "Trasladar", onAtras = onCerrar, cerrar = true)
        Segmentado(listOf("Baja", "Traslado"), modo, { modo = it })
        Tarjeta(radio = 24.dp) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(p.name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() },
                    fondo = fondo, tinta = tinta, lado = 48.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(p.name, style = Texto.TitleS, color = TintaBase)
                    Text(
                        listOfNotNull(p.daycare?.name, p.position?.name).joinToString(" · "),
                        style = Texto.BodyS, color = TintaSuave
                    )
                }
            }
        }
        if (modo == 0) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Motivo de baja", style = Texto.LabelM, color = TintaSuave)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    motivos.forEachIndexed { i, m ->
                        ChipOpcion(m.name, seleccionado = motivo == i, onClick = { motivo = i })
                    }
                }
            }
            Nota(
                "La baja aplica hoy. La persona sale de la plantilla activa y su historial se conserva.",
                icono = Ico.History, fondo = Mostaza100, tint = TintaBase,
            )
        } else {
            if (puedeTrasladar) {
                CampoOpciones(
                    "Estancia de destino", otras[destino].name, otras.map { it.name },
                    inicio = { Icono(Ico.Building, tint = TintaSuave, tamano = 20.dp) },
                    onElegido = { destino = it },
                )
            } else {
                Text("No hay otra estancia disponible.", style = Texto.BodyM, color = TintaSuave)
            }
            Nota(
                "El traslado aplica hoy. Su horario actual no se mueve: deberá asignarlo la nueva responsable.",
                icono = Ico.History, fondo = Mostaza100, tint = TintaBase,
            )
        }
        accion.error?.let { Nota(it, icono = Ico.Alert, fondo = Rojo100, tint = TintaBase) }
    }
}
