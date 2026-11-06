package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.FiltroDesplegable
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.BotonFlotanteIcono
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600
import com.example.access_tracker.ui.theme.Texto

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResAsistenciaScreen(
    onAvisos: () -> Unit,
    onTurnoCompleto: () -> Unit,
    onAusencia: (persona: String?) -> Unit,
    barra: @Composable () -> Unit = {},
) {
    var turno by remember { mutableIntStateOf(0) }
    PantallaBase(
        barra = barra,
        separacion = 16.dp,
        botonFlotante = { BotonFlotanteIcono(Ico.Plus, Coral500, { onAusencia(null) }, Modifier.padding(end = 8.dp)) },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) { TituloPantalla("Asistencia", subtitulo = "Miércoles 7 de octubre") }
            BotonCampana(3, onAvisos)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Segmentado(listOf("Matutino", "Vespertino"), turno, { turno = it }, Modifier.weight(1f))
            FiltroDesplegable(
                "Todas las salas",
                listOf("Todas las salas", "Lactantes", "Maternal A", "Maternal B", "Preescolar")
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Chip("3 presentes", tono = TonoChip.Exito)
            Chip("1 retardo", tono = TonoChip.Alerta)
            Chip("1 sin checar", tono = TonoChip.Neutro)
            Chip("1 ausente", tono = TonoChip.Peligro)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Petroleo700)
                .clickable(onClick = onTurnoCompleto)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Users, tint = Mostaza500)
            Text(
                "Registrar entrada de turno completo",
                style = Texto.LabelL,
                color = FondoCrema,
                modifier = Modifier.weight(1f)
            )
            Icono(Ico.Chevron, tint = FondoCrema, tamano = 20.dp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilaPersona(
                "AT",
                "Ana Torres",
                "Maternal A · Entrada 7:25",
                onClick = { onAusencia("Ana Torres") }
            ) {
                Chip("Retardo", tono = TonoChip.Alerta)
            }
            FilaPersona(
                "LH",
                "Lucía Herrera",
                "Lactantes · Entrada 6:58",
                onClick = { onAusencia("Lucía Herrera") },
                fondoAvatar = Mostaza100,
                tintaAvatar = TintaBase
            ) {
                Chip("Presente", tono = TonoChip.Exito)
            }
            FilaPersona(
                "CR",
                "Carmen Ruiz",
                "Maternal B · Entrada 7:02",
                onClick = { onAusencia("Carmen Ruiz") },
                fondoAvatar = Coral100,
                tintaAvatar = TintaBase
            ) {
                Chip("Presente", tono = TonoChip.Exito)
            }
            FilaPersona(
                "SD",
                "Sofía Díaz",
                "Preescolar · Sin checar",
                onClick = { onAusencia("Sofía Díaz") },
                fondoAvatar = Verde100,
                tintaAvatar = Verde600
            ) {
                Chip("Sin checar", tono = TonoChip.Neutro)
            }
            FilaPersona(
                "PR",
                "Paola Reyes",
                "Maternal A · Ausente",
                onClick = { onAusencia("Paola Reyes") }
            ) {
                Chip("Ausente", tono = TonoChip.Peligro)
            }
            FilaPersona(
                "RM",
                "Rosa Medina",
                "Cocina · Entrada 6:45",
                onClick = { onAusencia("Rosa Medina") },
                fondoAvatar = Mostaza100,
                tintaAvatar = TintaBase
            ) {
                Chip("Presente", tono = TonoChip.Exito)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResAsistenciaPreview() {
    Access_trackerTheme { ResAsistenciaScreen({}, {}, {}) }
}
