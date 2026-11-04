package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

private val Dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val Opciones = listOf("Matutino" to Ico.Sun, "Vespertino" to Ico.Moon, "Descanso" to Ico.Coffee)

/** Define qué días va una persona y en qué turno. 0 = matutino, 1 = vespertino, 2 = descanso. */
@Composable
fun ResHorarioPersonaScreen(onAtras: () -> Unit, onGuardar: () -> Unit) {
    val turnos = remember { mutableStateListOf(0, 0, 0, 0, 0, 2, 2) }
    val diasQueVa = turnos.count { it != 2 }
    PantallaBase(
        separacion = 14.dp,
        pie = { Boton("Guardar horario", onGuardar) },
    ) {
        EncabezadoPantalla("Horario de la persona", onAtras = onAtras, eyebrow = "Horarios")
        Tarjeta(radio = 24.dp) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Avatar("AT", lado = 48.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Ana Torres", style = Texto.TitleS, color = TintaBase)
                    Text("Maternal A · Maestra titular", style = Texto.BodyS, color = TintaSuave)
                }
                Chip("$diasQueVa días", tono = TonoChip.Info, punto = false)
            }
        }
        Text("Turno por día", style = Texto.LabelM, color = TintaSuave)
        Dias.forEachIndexed { i, dia ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(dia, style = Texto.LabelL, color = TintaBase)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Opciones.forEachIndexed { t, (nombre, icono) ->
                        ChipOpcion(nombre, Modifier.weight(1f), seleccionado = turnos[i] == t, icono = icono, onClick = { turnos[i] = t })
                    }
                }
            }
        }
        Nota("El horario se usa para saber quién debería estar cada día y avisar si una sala queda sin cubrir.", icono = Ico.Calendar)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResHorarioPersonaPreview() {
    Access_trackerTheme { ResHorarioPersonaScreen({}, {}) }
}
