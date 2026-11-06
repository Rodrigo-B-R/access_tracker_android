package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600
import androidx.compose.foundation.layout.Column

@Composable
fun CambioPasswordScreen(onGuardar: () -> Unit) {
    var temporal by remember { mutableStateOf("12345678") }
    var nueva by remember { mutableStateOf("Cristal2026") }
    var confirmar by remember { mutableStateOf("Cristal2026") }

    PantallaBase(
        separacion = 16.dp,
        pie = { Boton("Guardar y continuar", onClick = onGuardar, estilo = EstiloBoton.Oscuro) },
    ) {
        IconoCuadro(Ico.Lock)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Crea tu contraseña", style = Texto.TitleL, color = TintaBase)
            Text("Entraste con una contraseña temporal. Cámbiala para continuar.", style = Texto.BodyL, color = TintaSuave)
        }
        CampoTexto("Contraseña temporal", temporal, { temporal = it }, esPassword = true)
        CampoTexto("Nueva contraseña", nueva, { nueva = it }, esPassword = true)
        CampoTexto("Confirmar nueva contraseña", confirmar, { confirmar = it }, esPassword = true)
        Tarjeta {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tu contraseña debe tener", style = Texto.LabelM, color = TintaSuave)
                Requisito("Al menos 8 caracteres", cumplido = true)
                Requisito("Una mayúscula y una minúscula", cumplido = true)
                Requisito("Un número", cumplido = true)
                Requisito("No ser igual a la temporal", cumplido = false)
            }
        }
    }
}

@Composable
private fun Requisito(texto: String, cumplido: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(22.dp).clip(RoundedCornerShape(11.dp)).background(if (cumplido) Verde100 else FondoArena),
            contentAlignment = Alignment.Center,
        ) { if (cumplido) Icono(Ico.Check, tint = Verde600, tamano = 14.dp) }
        Text(texto, style = Texto.BodyM, color = if (cumplido) TintaBase else TintaSuave)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun CambioPasswordPreview() {
    Access_trackerTheme { CambioPasswordScreen({}) }
}
