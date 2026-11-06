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
import androidx.compose.foundation.layout.width
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

/** Pestaña Checar, la que abre al entrar. Estado: fuera de la estancia. */
@Composable
fun ChecarFueraScreen(onAvisos: () -> Unit, onRegistrarEntrada: () -> Unit, onCambiarPassword: () -> Unit = {}, onCerrarSesion: () -> Unit = {}, barra: @Composable () -> Unit = {}) {
    PantallaBase(barra = barra, conBarra = true) {
        BarraSuperiorLogo(avisos = 2, onAvisos = onAvisos, cuenta = { MenuCuenta(onCambiarPassword, onCerrarSesion) })
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Miércoles 7 de octubre", style = Texto.BodyL, color = TintaSuave)
            Text("Hola, María", style = Texto.DisplayL, color = TintaBase)
        }
        TurnoDeHoy()
        Column(
            Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(16.dp).border(2.5.dp, TintaBase, CircleShape))
                Text("Estás fuera", style = Texto.TitleM, color = TintaBase)
            }
            Text("Última checada: ayer 15:02 (salida)", style = Texto.BodyM, color = TintaSuave)
        }
        BotonChecada("Registrar\nentrada", Ico.Login, Coral500, Coral100, onRegistrarEntrada)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            PildoraEstado("En la zona · válida", Ico.Pin, Verde100, Verde600)
            PildoraEstado("Sincronizado", Ico.Cloud, FondoArena, TintaSuave)
        }
    }
}

/** Estado después de registrar la entrada: hora grande, avance de la jornada y botón de salida. */
@Composable
fun ChecarDentroScreen(onAvisos: () -> Unit, onRegistrarSalida: () -> Unit, onCambiarPassword: () -> Unit = {}, onCerrarSesion: () -> Unit = {}, barra: @Composable () -> Unit = {}) {
    PantallaBase(barra = barra, fondo = Petroleo700, conBarra = true) {
        BarraSuperiorLogo(avisos = 2, onAvisos = onAvisos, oscuro = true, cuenta = { MenuCuenta(onCambiarPassword, onCerrarSesion, oscuro = true) })
        Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(14.dp).clip(CircleShape).background(Mostaza500))
            Text("Estás dentro", style = Texto.TitleM, color = FondoCrema)
        }
        Column(Modifier.fillMaxWidth()) {
            Text("7:02", style = Texto.DisplayXL, color = FondoCrema)
            Text("Entrada registrada en la estancia · Maternal A", style = Texto.BodyL, color = Petroleo100)
        }
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(8) { i ->
                    Box(Modifier.weight(1f).height(14.dp).clip(RoundedCornerShape(7.dp)).background(if (i < 2) Mostaza500 else Petroleo500))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("7:00", style = Texto.LabelM, color = Petroleo100)
                Text("2 de 8 h", style = Texto.LabelM, color = FondoCrema)
                Text("15:00", style = Texto.LabelM, color = Petroleo100)
            }
        }
        BotonChecada("Registrar\nsalida", Ico.Logout, Mostaza500, Petroleo500, onRegistrarSalida)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            PildoraEstado("En la zona · válida", Ico.Pin, Petroleo500, FondoCrema)
            PildoraEstado("Enviado a las 7:02", Ico.Cloud, Petroleo500, FondoCrema)
        }
    }
}

/** Funciona sin conexión; marca «fuera de zona» sin bloquear para que la Responsable lo revise. */
@Composable
fun ChecarSinInternetScreen(onAvisos: () -> Unit, onRegistrarSalida: () -> Unit, onCambiarPassword: () -> Unit = {}, onCerrarSesion: () -> Unit = {}, barra: @Composable () -> Unit = {}) {
    PantallaBase(barra = barra, conBarra = true, separacion = 16.dp) {
        BarraSuperiorLogo(avisos = 2, onAvisos = onAvisos, cuenta = { MenuCuenta(onCambiarPassword, onCerrarSesion) })
        BannerConexion(EstadoBanner.SinConexion, "Sin conexión: tus registros se guardan en el dispositivo y se envían al reconectar.")
        TurnoDeHoy()
        Column(
            Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(Ico.Check, tint = Verde600, tamano = 22.dp)
                Text("Entrada guardada", style = Texto.TitleM, color = TintaBase)
            }
            Text("Registrada hoy a las 7:01 en este dispositivo", style = Texto.BodyM, color = TintaSuave, textAlign = TextAlign.Center)
        }
        BotonChecada("Registrar\nsalida", Ico.Logout, Mostaza500, Mostaza100, onRegistrarSalida)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            PildoraEstado("Fuera de zona", Ico.Pin, Coral100, TintaBase)
            PildoraEstado("Pendiente de sincronizar", Ico.Cloud, FondoArena, TintaSuave)
        }
    }
}

@Composable
private fun TurnoDeHoy() {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TarjetaDato("Sala", "Maternal A", Modifier.weight(1.3f), fondo = Mostaza500, tinta = TintaBase, estiloValor = Texto.TitleS)
        TarjetaDato("Entras", "7:00", Modifier.weight(1f))
        TarjetaDato("Sales", "15:00", Modifier.weight(1f))
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
