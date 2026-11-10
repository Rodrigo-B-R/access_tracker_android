package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoArenaFuerte
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

enum class EstadoDia { Normal, Hoy, Descanso }

/** Un dia de la semana tal como se pinta. */
data class DiaVista(
    val letra: String,
    val numero: String,
    val titulo: String,
    val horas: String,
    val sala: String?,
    val estado: EstadoDia,
)

/** Version de demostracion (indice de pantallas): datos fijos. */
@Composable
fun HorarioScreen(barra: @Composable () -> Unit = {}) {
    HorarioContenido(
        semana = "Semana del 5 al 9 de octubre",
        dias = listOf(
            DiaVista("L", "5", "Lunes 5", "7:00 – 15:00", "Sala Maternal A", EstadoDia.Normal),
            DiaVista("M", "6", "Martes 6", "7:00 – 15:00", "Sala Maternal A", EstadoDia.Normal),
            DiaVista("M", "7", "Miércoles 7", "7:00 – 15:00", "Sala Maternal A", EstadoDia.Hoy),
            DiaVista("J", "8", "Jueves 8", "Descanso", null, EstadoDia.Descanso),
            DiaVista("V", "9", "Viernes 9", "9:00 – 17:00", "Sala Lactantes", EstadoDia.Normal),
        ),
        barra = barra,
    )
}

@Composable
fun HorarioContenido(semana: String, dias: List<DiaVista>, barra: @Composable () -> Unit = {}) {
    PantallaBase(barra = barra, separacion = 16.dp) {
        TituloPantalla("Mi horario")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            dias.forEach { d -> DiaSemana(d.letra, d.numero, d.estado, Modifier.weight(1f)) }
        }
        Text(semana, style = Texto.BodyM, color = TintaSuave)
        dias.forEach { d -> Turno(d.titulo, d.horas, d.sala, d.estado) }
    }
}

@Composable
private fun DiaSemana(letra: String, numero: String, estado: EstadoDia, modifier: Modifier = Modifier) {
    val fondo = when (estado) { EstadoDia.Hoy -> Mostaza500; EstadoDia.Descanso -> FondoArena; else -> FondoBlanco }
    val punto = when (estado) {
        EstadoDia.Hoy -> TintaBase
        EstadoDia.Descanso -> FondoArenaFuerte
        else -> Petroleo700
    }
    Column(
        modifier.clip(RoundedCornerShape(22.dp)).background(fondo).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(letra, style = Texto.LabelS, color = if (estado == EstadoDia.Hoy) TintaBase else TintaSuave)
        Text(numero, style = Texto.TitleM, color = TintaBase)
        Box(Modifier.size(6.dp).clip(CircleShape).background(punto))
    }
}

@Composable
private fun Turno(dia: String, horas: String, sala: String?, estado: EstadoDia = EstadoDia.Normal) {
    val hoy = estado == EstadoDia.Hoy
    val descanso = estado == EstadoDia.Descanso
    val fondo: Color = if (hoy) Petroleo700 else if (descanso) FondoArena else FondoBlanco
    val tintaPrincipal = if (hoy) FondoCrema else TintaBase
    val tintaSuave = if (hoy) Petroleo100 else TintaSuave
    Tarjeta(fondo = fondo, relleno = 0.dp) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconoCuadro(
                icono = if (descanso) Ico.Coffee else Ico.Sun,
                fondo = if (hoy) Petroleo500 else if (descanso) FondoBlanco else Petroleo100,
                tint = if (hoy) Mostaza500 else TintaBase,
                lado = 40.dp, radio = 12.dp, tamanoIcono = 20.dp,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(dia, style = Texto.LabelS, color = tintaSuave)
                Text(horas, style = Texto.TitleS, color = tintaPrincipal)
            }
            if (sala != null) Text(sala, style = Texto.LabelM, color = if (hoy) FondoCrema else TintaSuave)
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun HorarioPreview() {
    Access_trackerTheme { HorarioScreen() }
}
