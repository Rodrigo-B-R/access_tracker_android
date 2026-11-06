package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import androidx.compose.ui.graphics.Color

@Composable
fun PermisosScreen(onContinuar: () -> Unit) {
    PantallaBase(
        separacion = 16.dp,
        pie = {
            Boton("Permitir y continuar", onClick = onContinuar)
            Boton("Ahora no", onClick = onContinuar, estilo = EstiloBoton.Secundario)
        },
    ) {
        IconoCuadro(Ico.Pin)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Antes de empezar", style = Texto.TitleL, color = TintaBase)
            Text("La app solo pide lo que necesita para funcionar.", style = Texto.BodyL, color = TintaSuave)
        }
        PermisoFila(Ico.Pin, Mostaza100, "Ubicación", "Para comprobar que checas en la estancia. Solo se usa al registrar tu entrada o salida.")
        PermisoFila(Ico.Bell, Coral100, "Notificaciones", "Para avisarte de cambios de horario, reuniones y avisos urgentes.")
        Nota("Si no das el permiso de ubicación, tus registros se guardan con la marca «sin ubicación» para que tu Responsable los revise.")
    }
}

@Composable
private fun PermisoFila(icono: Ico, fondo: Color, titulo: String, texto: String) {
    Tarjeta(radio = 24.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            IconoCuadro(icono, fondo = fondo, lado = 44.dp, radio = 14.dp, tamanoIcono = 24.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(titulo, style = Texto.TitleS, color = TintaBase)
                Text(texto, style = Texto.BodyM, color = TintaSuave)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun PermisosPreview() {
    Access_trackerTheme { PermisosScreen({}) }
}
