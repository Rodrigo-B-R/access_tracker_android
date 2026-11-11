package com.example.access_tracker.screens.empleada

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.Gps
import com.example.access_tracker.data.Pendiente
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import java.time.Duration
import java.time.Instant

/** Pantalla "Checar" con datos reales: decide entre fuera y dentro segun /time-records/status. */
@Composable
fun ChecarRoute(
    onAvisos: () -> Unit,
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit,
    barra: @Composable () -> Unit,
) {
    val vm: ChecarViewModel = viewModel()
    val st by vm.estado.collectAsState()
    val contexto = LocalContext.current

    LaunchedEffect(Unit) { vm.cargar() }

    // Acepte o rechace el permiso, se registra la entrada: sin ubicacion queda "fuera de zona".
    val pedirUbicacion = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        vm.registrarEntrada()
    }
    val onEntrada = {
        if (Gps.tienePermiso(contexto)) vm.registrarEntrada()
        else pedirUbicacion.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    val datos = st.datos
    if (datos == null) {
        PantallaBase(barra = barra) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    st.errorCarga ?: "Cargando…",
                    style = Texto.TitleM,
                    color = if (st.errorCarga != null) TintaBase else TintaSuave,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (st.errorCarga != null) {
                    Boton("Reintentar", onClick = { vm.cargar() }, estilo = EstiloBoton.Oscuro)
                }
            }
        }
        return
    }

    val abierta = datos.openRecord
    // Lo ultimo que la persona hizo sin internet manda sobre lo que dice la copia del servidor.
    val ultima = st.pendientes.lastOrNull()
    val turnoHoy = if (st.datosDeOtroDia) null else datos.todayShifts.firstOrNull()?.let {
        DatosTurno(it.room.name, Fechas.horaCorta(it.shift.startTime), Fechas.horaCorta(it.shift.endTime))
    }
    val sinTurnoTexto = if (st.datosDeOtroDia) {
        "Sin conexión: tu turno se mostrará al reconectar"
    } else {
        "Hoy no tienes turno asignado"
    }

    when {
        // Entrada guardada en el telefono, todavia sin enviar: se puede registrar la salida igual.
        ultima?.tipo == Pendiente.TIPO_ENTRADA -> ChecarSinInternetScreen(
            onAvisos = onAvisos,
            onRegistrarSalida = { vm.registrarSalida() },
            onCambiarPassword = onCambiarPassword,
            onCerrarSesion = onCerrarSesion,
            barra = barra,
            horaEntrada = Fechas.horaLocal(ultima.ocurridoEn),
            turno = turnoHoy,
            sinTurnoTexto = sinTurnoTexto,
            textoZona = "Zona: se valida al enviar",
            enviando = st.enviando,
            avisosSinLeer = st.avisosSinLeer,
        )
        ultima == null && datos.inside && abierta != null -> {
            val inicio = Instant.parse(abierta.clockInAt)
            val horasTurno = (abierta.shift.endTime.take(2).toInt() - abierta.shift.startTime.take(2).toInt())
                .coerceAtLeast(1)
            val transcurridas = Duration.between(inicio, Instant.now()).toHours().toInt().coerceIn(0, horasTurno)
            ChecarDentroScreen(
                onAvisos = onAvisos,
                onRegistrarSalida = { vm.registrarSalida() },
                onCambiarPassword = onCambiarPassword,
                onCerrarSesion = onCerrarSesion,
                barra = barra,
                horaEntrada = Fechas.horaLocal(abierta.clockInAt),
                sala = abierta.room.name,
                horaInicioTurno = Fechas.horaCorta(abierta.shift.startTime),
                horaFinTurno = Fechas.horaCorta(abierta.shift.endTime),
                horasTranscurridas = transcurridas,
                horasTotales = horasTurno,
                enZona = abierta.withinZone,
                mensaje = listOfNotNull(
                    st.mensaje,
                    if (st.sinConexion) "Sin conexión: tu salida se guardará en el dispositivo." else null,
                ).joinToString("\n").ifBlank { null },
                enviando = st.enviando,
                avisosSinLeer = st.avisosSinLeer,
            )
        }
        else -> ChecarFueraScreen(
            onAvisos = onAvisos,
            onRegistrarEntrada = onEntrada,
            onCambiarPassword = onCambiarPassword,
            onCerrarSesion = onCerrarSesion,
            barra = barra,
            fecha = Fechas.fechaLarga(),
            nombre = ServiceLocator.sesion.leer()?.name?.substringBefore(' ') ?: "",
            turno = turnoHoy,
            mensaje = listOfNotNull(
                // La ultima checada guardada sin enviar fue una salida.
                ultima?.let { "Salida guardada a las ${Fechas.horaLocal(it.ocurridoEn)}. Se enviará al reconectar." },
                st.mensaje,
            ).joinToString("\n").ifBlank { null },
            enviando = st.enviando,
            avisosSinLeer = st.avisosSinLeer,
            sinTurnoTexto = sinTurnoTexto,
            bannerSinConexion = if (st.sinConexion) {
                "Sin conexión: tus registros se guardan en el dispositivo y se envían al reconectar."
            } else null,
        )
    }
}
