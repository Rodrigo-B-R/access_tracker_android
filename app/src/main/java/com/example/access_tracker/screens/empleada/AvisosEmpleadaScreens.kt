package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.AccionAviso
import com.example.access_tracker.components.AvisoItem
import com.example.access_tracker.components.AvisoUrgente
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.EtiquetaUrgente
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.LecturaConfirmada
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.SinLeer
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

@Composable
fun AvisosBandejaScreen(onAtras: () -> Unit, onAbrirUrgente: () -> Unit) {
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Avisos", onAtras = onAtras, grande = true)
        AvisoUrgente(
            titulo = "Cierre anticipado por lluvia",
            texto = "Se recoge a las niñas y niños a las 13:00. Avisa a las familias.",
            hace = "Hace 10 min",
            onClick = onAbrirUrgente,
        ) { AccionAviso("Pide confirmar lectura", Ico.Eye, onClick = onAbrirUrgente) }
        Eyebrow("Anteriores")
        AvisoItem("Cambio de horario", TonoChip.Alerta, "Ayer", "Nuevo horario de comida desde el lunes", sinLeer = true) { SinLeer() }
        AvisoItem("Reunión", TonoChip.Info, "Lun 5 oct", "Reunión de personal el viernes a las 9:00") { LecturaConfirmada() }
        AvisoItem("Recordatorio", TonoChip.Neutro, "Vie 2 oct", "Entregar las bitácoras al cierre del turno") { LecturaConfirmada() }
    }
}

@Composable
fun AvisoDetalleScreen(onAtras: () -> Unit, onConfirmar: () -> Unit) {
    PantallaBase(
        separacion = 16.dp,
        cabecera = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(Rojo600)
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                EncabezadoPantalla("Aviso", onAtras = onAtras, fondoBoton = FondoBlanco, tinta = FondoBlanco)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    EtiquetaUrgente(relleno = 12)
                    Text("Hoy 10:12", style = Texto.BodyM, color = FondoBlanco)
                }
                Text("Cierre anticipado por lluvia", style = Texto.DisplayL, color = FondoBlanco)
                Row(Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(FondoBlanco), contentAlignment = Alignment.Center) {
                        Text("MG", style = Texto.LabelS, color = Rojo600)
                    }
                    Text("De: Responsable · Estancia Cumbres", style = Texto.LabelM, color = FondoBlanco)
                }
            }
        },
        pie = { Boton("Confirmar lectura", onConfirmar, estilo = EstiloBoton.Oscuro) },
    ) {
        Text(
            "Por la alerta de lluvia fuerte, hoy cerramos a las 13:00. Las niñas y niños se entregan a partir de las 12:30.\n\n" +
                "Avisen a las familias y revisen que no quede nadie en las salas antes de salir.",
            style = Texto.BodyL, color = TintaBase,
        )
        Nota("La Responsable ve quién ya confirmó la lectura.", icono = Ico.Eye, fondo = FondoArena, tint = TintaSuave, colorTexto = TintaSuave)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun AvisosBandejaPreview() {
    Access_trackerTheme { AvisosBandejaScreen({}, {}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun AvisoDetallePreview() {
    Access_trackerTheme { AvisoDetalleScreen({}, {}) }
}
