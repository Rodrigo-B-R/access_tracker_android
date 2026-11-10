package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.data.ReglasPassword
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

/**
 * Cambio voluntario de contrasena (menu de cuenta). Los requisitos se marcan mientras se escribe;
 * la API vuelve a validarlos al guardar.
 */
@Composable
fun CambioPasswordScreen(
    onGuardar: (actual: String, nueva: String) -> Unit,
    onCancelar: () -> Unit = {},
    enviando: Boolean = false,
    error: String? = null,
) {
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    val requisitos = ReglasPassword.requisitos(nueva, actual, confirmar)
    val valida = ReglasPassword.valida(nueva, actual, confirmar)

    PantallaBase(
        separacion = 16.dp,
        pie = {
            Boton(
                if (enviando) "Guardando…" else "Guardar contraseña",
                onClick = { onGuardar(actual, nueva) },
                estilo = EstiloBoton.Oscuro,
                habilitado = valida && !enviando,
            )
            Boton("Cancelar", onClick = onCancelar, estilo = EstiloBoton.Secundario, habilitado = !enviando)
        },
    ) {
        IconoCuadro(Ico.Lock)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Cambiar contraseña", style = Texto.TitleL, color = TintaBase)
            Text(
                "Escribe tu contraseña actual y elige una nueva.",
                style = Texto.BodyL,
                color = TintaSuave
            )
        }
        // Topes = limites de la API (PasswordChangeIn: 256).
        CampoTexto(
            "Contraseña actual", actual, { actual = it }, esPassword = true,
            maxCaracteres = 256, imeAction = ImeAction.Next,
        )
        CampoTexto(
            "Nueva contraseña", nueva, { nueva = it }, esPassword = true,
            maxCaracteres = 256, imeAction = ImeAction.Next,
        )
        CampoTexto(
            "Confirmar nueva contraseña", confirmar, { confirmar = it }, esPassword = true,
            maxCaracteres = 256, imeAction = ImeAction.Done,
        )
        if (error != null) Text(error, style = Texto.BodyM, color = Coral500)
        Tarjeta {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tu contraseña nueva debe tener", style = Texto.LabelM, color = TintaSuave)
                requisitos.forEach { Requisito(it.texto, it.cumplido) }
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
    Access_trackerTheme { CambioPasswordScreen({ _, _ -> }) }
}
