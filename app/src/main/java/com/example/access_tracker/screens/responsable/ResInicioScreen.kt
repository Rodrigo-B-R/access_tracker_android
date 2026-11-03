package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.BotonCircular
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.SalaMini
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde600

/** Pestaña Inicio de la Responsable: alertas, indicadores del día y cobertura por sala. */
@Composable
fun ResInicioScreen(
    onAvisos: () -> Unit,
    onMenu: () -> Unit,
    onAtender: () -> Unit,
    onVerAsistencia: () -> Unit,
) {
    PantallaBase(conBarra = true, separacion = 20.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Estancia Cumbres", style = Texto.TitleM, color = TintaBase)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icono(Ico.Cloud, tint = Verde600, tamano = 16.dp)
                    Text("Sincronizado · 9:08", style = Texto.LabelM, color = Verde600)
                }
            }
            BotonCampana(contador = 3, onClick = onAvisos)
            BotonCircular(Ico.More, onMenu)
        }
        // Alerta
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Rojo600).padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Alert, tint = FondoBlanco, tamano = 28.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Maternal A bajo el mínimo", style = Texto.TitleS, color = FondoBlanco)
                Text("2 de 3 presentes hoy", style = Texto.BodyM, color = FondoBlanco)
            }
            Text(
                "Atender", style = Texto.LabelM, color = TintaBase,
                modifier = Modifier.clip(RoundedCornerShape(22.dp)).background(FondoBlanco).clickable(onClick = onAtender).padding(horizontal = 16.dp, vertical = 11.dp),
            )
        }
        // Indicadores
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(28.dp)).background(Petroleo700).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Presentes hoy", style = Texto.LabelM, color = FondoCrema)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                    Text("14", style = Texto.DisplayL, color = FondoCrema)
                    Text("/16", style = Texto.TitleM, color = Petroleo100)
                }
                Text("2 sin checar", style = Texto.BodyS, color = Petroleo100)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Eyebrow("Cobertura por sala")
            Text("Ver asistencia", style = Texto.LabelM, color = Petroleo700, modifier = Modifier.clickable(onClick = onVerAsistencia))
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SalaMini("Lactantes", 4, 4, Modifier.weight(1f))
                SalaMini("Maternal A", 2, 3, Modifier.weight(1f), bajoMinimo = true)
            }
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SalaMini("Maternal B", 3, 3, Modifier.weight(1f))
                SalaMini("Preescolar", 5, 5, Modifier.weight(1f))
            }
        }
        Tarjeta(radio = 24.dp, onClick = onAtender) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                IconoCuadro(Ico.Flag, fondo = Coral100, lado = 44.dp, radio = 14.dp, tamanoIcono = 24.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Turno sin cubrir · Maternal A", style = Texto.LabelL, color = TintaBase)
                    Text("7:00 – 15:00 · falta 1 persona. Marcado por la Asistente.", style = Texto.BodyS, color = TintaSuave)
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResInicioPreview() {
    Access_trackerTheme { ResInicioScreen({}, {}, {}, {}) }
}
