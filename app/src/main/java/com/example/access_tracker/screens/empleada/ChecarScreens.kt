package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.BannerConexion
import com.example.access_tracker.components.BarraSuperiorLogo
import com.example.access_tracker.components.BotonChecada
import com.example.access_tracker.components.EstadoBanner
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.MenuCuenta
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.PildoraEstado
import com.example.access_tracker.components.TarjetaDato
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

/** Turno de hoy tal como se muestra en pantalla. */
data class DatosTurno(val sala: String, val entra: String, val sale: String)

@Composable
fun ChecarFueraScreen(
    onAvisos: () -> Unit,
    onRegistrarEntrada: () -> Unit,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {},
    fecha: String = "Miércoles 7 de octubre",
    nombre: String = "María",
    turno: DatosTurno? = DatosTurno("Maternal A", "7:00", "15:00"),
    mensaje: String? = null,
    enviando: Boolean = false,
    avisosSinLeer: Int = 2,
    sinTurnoTexto: String = "Hoy no tienes turno asignado",
    /** Si no es null, se muestra el banner de "sin conexion" con este texto. */
    bannerSinConexion: String? = null,
) {
    PantallaBase(barra = barra) {
        BarraSuperiorLogo(avisos = avisosSinLeer, onAvisos = onAvisos, cuenta = { MenuCuenta(onCambiarPassword, onCerrarSesion) })
        if (bannerSinConexion != null) BannerConexion(EstadoBanner.SinConexion, bannerSinConexion)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(fecha, style = Texto.BodyL, color = TintaSuave)
            Text("Hola, $nombre", style = Texto.DisplayL, color = TintaBase)
        }
        TurnoDeHoy(turno, sinTurnoTexto)
        Column(
            Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(16.dp).border(2.5.dp, TintaBase, CircleShape))
                Text("Estás fuera", style = Texto.TitleM, color = TintaBase)
            }
            if (mensaje != null) {
                Text(mensaje, style = Texto.BodyM, color = Coral500, textAlign = TextAlign.Center)
            }
        }
        BotonChecada(
            if (enviando) "Registrando…" else "Registrar\nentrada",
            Ico.Login, Coral500, Coral100, onRegistrarEntrada, habilitado = !enviando,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            PildoraEstado("Se valida tu ubicación", Ico.Pin, Verde100, Verde600)
            PildoraEstado("Sincronizado", Ico.Cloud, FondoArena, TintaSuave)
        }
    }
}

@Composable
fun ChecarDentroScreen(
    onAvisos: () -> Unit,
    onRegistrarSalida: () -> Unit,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {},
    horaEntrada: String = "7:02",
    sala: String = "Maternal A",
    horaInicioTurno: String = "7:00",
    horaFinTurno: String = "15:00",
    horasTranscurridas: Int = 2,
    horasTotales: Int = 8,
    enZona: Boolean = true,
    mensaje: String? = null,
    enviando: Boolean = false,
    avisosSinLeer: Int = 2,
) {
    PantallaBase(barra = barra, fondo = Petroleo700) {
        BarraSuperiorLogo(
            avisos = avisosSinLeer,
            onAvisos = onAvisos,
            oscuro = true,
            cuenta = { MenuCuenta(onCambiarPassword, onCerrarSesion, oscuro = true) }
        )
        Row(
            Modifier.padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(14.dp).clip(CircleShape).background(Mostaza500))
            Text("Estás dentro", style = Texto.TitleM, color = FondoCrema)
        }
        Column(Modifier.fillMaxWidth()) {
            Text(horaEntrada, style = Texto.DisplayXL, color = FondoCrema)
            Text("Entrada registrada en la estancia · $sala", style = Texto.BodyL, color = Petroleo100)
        }
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(horasTotales) { i ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (i < horasTranscurridas) Mostaza500 else Petroleo500)
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(horaInicioTurno, style = Texto.LabelM, color = Petroleo100)
                Text("$horasTranscurridas de $horasTotales h", style = Texto.LabelM, color = FondoCrema)
                Text(horaFinTurno, style = Texto.LabelM, color = Petroleo100)
            }
        }
        if (mensaje != null) {
            Text(mensaje, style = Texto.BodyM, color = Mostaza500, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        BotonChecada(
            if (enviando) "Registrando…" else "Registrar\nsalida",
            Ico.Logout, Mostaza500, Petroleo500, onRegistrarSalida, habilitado = !enviando,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            PildoraEstado(
                if (enZona) "En la zona · válida" else "Fuera de zona",
                Ico.Pin, Petroleo500, FondoCrema,
            )
            PildoraEstado("Enviado a las $horaEntrada", Ico.Cloud, Petroleo500, FondoCrema)
        }
    }
}

@Composable
fun ChecarSinInternetScreen(
    onAvisos: () -> Unit,
    onRegistrarSalida: () -> Unit,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {},
    horaEntrada: String = "7:01",
    turno: DatosTurno? = DatosTurno("Maternal A", "7:00", "15:00"),
    sinTurnoTexto: String = "Hoy no tienes turno asignado",
    textoZona: String = "Fuera de zona",
    enviando: Boolean = false,
    avisosSinLeer: Int = 2,
) {
    PantallaBase(barra = barra, separacion = 16.dp) {
        BarraSuperiorLogo(avisos = avisosSinLeer, onAvisos = onAvisos, cuenta = { MenuCuenta(onCambiarPassword, onCerrarSesion) })
        BannerConexion(
            EstadoBanner.SinConexion,
            "Sin conexión: tus registros se guardan en el dispositivo y se envían al reconectar."
        )
        TurnoDeHoy(turno, sinTurnoTexto)
        Column(
            Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(Ico.Check, tint = Verde600, tamano = 22.dp)
                Text("Entrada guardada", style = Texto.TitleM, color = TintaBase)
            }
            Text(
                "Registrada hoy a las $horaEntrada en este dispositivo",
                style = Texto.BodyM,
                color = TintaSuave,
                textAlign = TextAlign.Center
            )
        }
        BotonChecada("Registrar\nsalida", Ico.Logout, Mostaza500, Mostaza100, onRegistrarSalida)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            PildoraEstado("Fuera de zona", Ico.Pin, Coral100, TintaBase)
            PildoraEstado("Pendiente de sincronizar", Ico.Cloud, FondoArena, TintaSuave)
        }
    }
}

@Composable
private fun TurnoDeHoy(
    turno: DatosTurno? = DatosTurno("Maternal A", "7:00", "15:00"),
    sinTurnoTexto: String = "Hoy no tienes turno asignado",
) {
    if (turno == null) {
        Text(sinTurnoTexto, style = Texto.TitleS, color = TintaSuave)
        return
    }
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TarjetaDato(
            "Sala",
            turno.sala,
            Modifier.weight(1.3f),
            fondo = Mostaza500,
            tinta = TintaBase,
            estiloValor = Texto.TitleS
        )
        TarjetaDato("Entras", turno.entra, Modifier.weight(1f))
        TarjetaDato("Sales", turno.sale, Modifier.weight(1f))
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ChecarFueraPreview() {
    Access_trackerTheme { ChecarFueraScreen({}, {}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ChecarDentroPreview() {
    Access_trackerTheme { ChecarDentroScreen({}, {}) }
}
