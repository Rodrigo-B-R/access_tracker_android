package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.AvisosPill
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.BarraSuperiorLogo
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.TarjetaDato
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

/** Modal sobre cualquier pantalla; aquí se muestra sobre el inicio de la Empleada. */
@Composable
fun SesionExpiradaScreen(onIniciarSesion: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        PantallaBase {
            BarraSuperiorLogo(avisos = 2, onAvisos = {})
            Text("Hola, María", style = Texto.DisplayL, color = TintaBase)
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaDato("Turno de hoy", "Matutino", Modifier.weight(1f), fondo = Mostaza500, tinta = TintaBase, estiloValor = Texto.TitleM)
                TarjetaDato("Horario", "7:00 – 15:00", Modifier.weight(1f), fondo = Petroleo700, tinta = FondoCrema, estiloValor = Texto.TitleM)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(14.dp).clip(CircleShape).background(Mostaza500))
                Text("Estás dentro", style = Texto.TitleM, color = TintaBase)
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(216.dp).clip(CircleShape).background(Coral500))
            }
        }
        Box(Modifier.fillMaxSize().background(TintaBase.copy(alpha = 0.55f)))
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(32.dp), ambientColor = TintaBase, spotColor = TintaBase)
                .clip(RoundedCornerShape(32.dp))
                .background(FondoCrema)
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconoCuadro(Ico.Clock, fondo = Coral100, lado = 64.dp, radio = 32.dp)
            Text("Tu sesión se cerró", style = Texto.TitleL, color = TintaBase, textAlign = TextAlign.Center)
            Text(
                "Se cerró por inactividad (15 min). Tus registros sin enviar siguen guardados en este dispositivo.",
                style = Texto.BodyL, color = TintaSuave, textAlign = TextAlign.Center,
            )
            Boton("Iniciar sesión", onClick = onIniciarSesion, estilo = EstiloBoton.Oscuro)
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun SesionExpiradaPreview() {
    Access_trackerTheme { SesionExpiradaScreen({}) }
}
