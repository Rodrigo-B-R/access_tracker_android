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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.Casilla
import com.example.access_tracker.components.Divisor
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.EventoBitacora
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.GrupoFilas
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResBitacoraScreen(onAtras: () -> Unit) {
    var filtro by remember { mutableIntStateOf(0) }
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Bitácora", onAtras = onAtras, eyebrow = "Estancia Cumbres")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todo", "Checadas", "Ausencias", "Altas y bajas", "Salas").forEachIndexed { i, t ->
                FiltroPill(t, activo = filtro == i, onClick = { filtro = i })
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EventoBitacora(
                Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info,
                "Entrada de Ana Torres · 7:00", "Registrado por Ana Torres · 7 oct 7:00",
            )
            EventoBitacora(
                Ico.Alert, Coral100, "Ausencia", TonoChip.Coral,
                "Paola Reyes · Maternal A", "Registrado por Responsable · 7 oct 8:02",
                motivo = "Motivo: enfermedad",
            )
            EventoBitacora(
                Ico.Userplus, Verde100, "Alta", TonoChip.Exito,
                "Marta Gil Soto · Maestra titular", "Registrado por Responsable · 6 oct 9:30",
            )
            EventoBitacora(
                Ico.Users, Mostaza100, "Salas", TonoChip.Alerta,
                "Preescolar: mínimo matutino 3 → 4", "Registrado por Responsable · 5 oct 16:10",
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResBitacoraPreview() {
    Access_trackerTheme { ResBitacoraScreen({}) }
}
