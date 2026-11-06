package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.BarraBusqueda
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.BotonFlotanteIcono
import com.example.access_tracker.components.EncabezadoSeccion
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.Flecha
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.sobreBarra
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

/** Pestaña Personal: lista agrupada por rol, con alta de personas. */
@Composable
fun ResPersonalScreen(onAvisos: () -> Unit, onPersona: () -> Unit, onAlta: () -> Unit, barra: @Composable () -> Unit = {}) {
    var vista by remember { mutableIntStateOf(0) }
    PantallaBase(
        barra = barra,
        conBarra = true,
        separacion = 16.dp,
        overlay = {
            BotonFlotanteIcono(Ico.Userplus, Mostaza500, onAlta, Modifier.align(Alignment.BottomEnd).sobreBarra())
        },
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { TituloPantalla("Personal", subtitulo = "16 personas activas") }
            BotonCampana(3, onAvisos)
        }
        BarraBusqueda("Buscar por nombre")
        Segmentado(listOf("Activos (16)", "Bajas (3)"), vista, { vista = it })
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EncabezadoSeccion("Maestras titulares · 3")
            FilaPersona("AT", "Ana Torres", "Maternal A", ladoAvatar = 40.dp, onClick = onPersona) { Flecha() }
            FilaPersona("LH", "Lucía Herrera", "Lactantes", fondoAvatar = Mostaza100, tintaAvatar = TintaBase, ladoAvatar = 40.dp, onClick = onPersona) { Flecha() }
            FilaPersona("CR", "Carmen Ruiz", "Maternal B", fondoAvatar = Coral100, tintaAvatar = TintaBase, ladoAvatar = 40.dp, onClick = onPersona) { Flecha() }
            EncabezadoSeccion("Asistentes · 2")
            FilaPersona("SD", "Sofía Díaz", "Preescolar", fondoAvatar = Verde100, tintaAvatar = Verde600, ladoAvatar = 40.dp, onClick = onPersona) { Flecha() }
            FilaPersona("PR", "Paola Reyes", "Maternal A", ladoAvatar = 40.dp, onClick = onPersona) { Flecha() }
            EncabezadoSeccion("Cocina · 1")
            FilaPersona("RM", "Rosa Medina", "Cocina", fondoAvatar = Mostaza100, tintaAvatar = TintaBase, ladoAvatar = 40.dp, onClick = onPersona) { Flecha() }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResPersonalPreview() {
    Access_trackerTheme { ResPersonalScreen({}, {}, {}) }
}
