package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.BannerConexion
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.EstadoBanner
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TituloConEyebrow
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Texto

@Composable
fun AvisoSinConexionScreen() {
    PantallaBase(separacion = 14.dp) {
        TituloConEyebrow("Componente", "Estados del banner")
        BannerConexion(EstadoBanner.SinConexion, "Sin conexión: tus registros se guardan en el dispositivo.")
        BannerConexion(EstadoBanner.Reconectado, "Reconectado: enviando 3 registros…")
        BannerConexion(EstadoBanner.Sincronizado, "Todo sincronizado. Sin duplicados.")
        Spacer(Modifier.height(6.dp))
        TituloConEyebrow("Componente", "Marca en cada registro")
        RegistroMarcado("Mié 7 oct · Entrada", "7:02 a. m.", "Pendiente de sincronizar", TonoChip.Neutro)
        RegistroMarcado("Mié 7 oct · Salida", "3:01 p. m.", "Pendiente de sincronizar", TonoChip.Neutro)
        RegistroMarcado("Mar 6 oct · Entrada", "7:12 a. m.", "Sincronizado", TonoChip.Exito)
    }
}

@Composable
private fun RegistroMarcado(titulo: String, hora: String, estado: String, tono: TonoChip) {
    Tarjeta {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(titulo, style = Texto.LabelL, color = TintaBase)
                Chip(estado, tono = tono)
            }
            Text(hora, style = Texto.TitleS, color = TintaBase)
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun AvisoSinConexionPreview() {
    Access_trackerTheme { AvisoSinConexionScreen() }
}
