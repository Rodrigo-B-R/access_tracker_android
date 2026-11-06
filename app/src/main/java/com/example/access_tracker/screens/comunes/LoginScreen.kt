package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Logo
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

@Composable
fun LoginScreen(
    onIniciarSesion: (clave: String) -> Unit,
    onVerIndice: () -> Unit,
) {
    var clave by remember { mutableStateOf("ana.torres") }
    var password by remember { mutableStateOf("1234567890") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Petroleo700)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MotivoPlantilla(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Logo(lado = 80.dp, sobreOscuro = true)
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        "Sonrisas\nde Cristal",
                        style = Texto.DisplayXL.copy(fontSize = 56.sp, lineHeight = 52.sp),
                        color = FondoCrema,
                    )
                    Text("Control de personal de las estancias", style = Texto.BodyL, color = Petroleo100)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(FondoCrema)
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            CampoTexto("Clave", clave, { clave = it }, icono = Ico.Idcard)
            CampoTexto("Contraseña", password, { password = it }, icono = Ico.Lock, esPassword = true)
            Boton("Iniciar sesión", onClick = { onIniciarSesion(clave) })
            Text(
                text = "¿Olvidaste tu contraseña? Pídela a tu Responsable.",
                style = Texto.BodyM,
                color = TintaSuave,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Ver índice de pantallas (demo)",
                style = Texto.BodyS,
                color = TintaTenue,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVerIndice() }
            )
        }
    }
}

@Composable
private fun MotivoPlantilla(modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(11.dp)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (fila in 0..3) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (col in 0..3) {
                    if (fila == 2 && col == 2) {
                        Box(Modifier.size(38.dp).border(2.dp, Coral500, forma))
                    } else {
                        val color = if (fila == col && fila % 2 == 1) Mostaza500 else Petroleo500
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(forma)
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun LoginPreview() {
    Access_trackerTheme { LoginScreen({}, {}) }
}
