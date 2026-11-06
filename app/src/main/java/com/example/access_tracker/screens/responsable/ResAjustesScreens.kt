package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.ContadorMinimo
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

@Composable
fun ResMenuScreen(
    onCerrar: () -> Unit,
    onBitacora: () -> Unit,
    onSalas: () -> Unit,
    onExportar: () -> Unit,
    onCuenta: () -> Unit,
    onCerrarSesion: () -> Unit,
    barra: @Composable () -> Unit = {},
) {
    Box(Modifier.fillMaxSize()) {
        ResInicioScreen(onAvisos = {}, onMenu = {}, onAtender = {}, onVerAsistencia = {}, barra = barra)
        Box(Modifier.fillMaxSize().background(TintaBase.copy(alpha = 0.45f)).clickable(onClick = onCerrar))
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 72.dp, end = 24.dp)
                .width(236.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp), ambientColor = TintaBase, spotColor = TintaBase)
                .clip(RoundedCornerShape(24.dp))
                .background(FondoBlanco)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            OpcionMenu(Ico.History, Petroleo100, "Bitácora", "Quién hizo qué", onBitacora)
            OpcionMenu(Ico.Users, Mostaza100, "Salas y mínimos", "Personal mínimo por turno", onSalas)
            OpcionMenu(Ico.Download, Coral100, "Exportar", "CSV de asistencias", onExportar)
            OpcionMenu(Ico.Lock, Petroleo100, "Mi cuenta", "Cambiar contraseña", onCuenta)
            OpcionMenu(Ico.Logout, Rojo100, "Cerrar sesión", "Salir de la cuenta", onCerrarSesion)
        }
    }
}

@Composable
private fun OpcionMenu(icono: Ico, fondo: androidx.compose.ui.graphics.Color, titulo: String, detalle: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconoCuadro(icono, fondo = fondo, lado = 40.dp, radio = 14.dp, tamanoIcono = 24.dp)
        Column(Modifier.weight(1f)) {
            Text(titulo, style = Texto.LabelL, color = TintaBase)
            Text(detalle, style = Texto.BodyS, color = TintaSuave)
        }
    }
}

/** Mínimo de personas por sala y turno. */
@Composable
fun ResAjustesSalasScreen(onAtras: () -> Unit, onGuardar: () -> Unit) {
    val salas = remember {
        mutableStateOf(
            listOf(
                Triple("Lactantes", 2, 2),
                Triple("Maternal A", 3, 2),
                Triple("Maternal B", 3, 2),
                Triple("Preescolar", 4, 3),
            ),
        )
    }
    PantallaBase(
        separacion = 14.dp,
        pie = { Boton("Guardar cambios", onGuardar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Salas y mínimos", onAtras = onAtras, eyebrow = "Menú")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            LeyendaTurno(Ico.Sun, "Mínimo matutino")
            LeyendaTurno(Ico.Moon, "Mínimo vespertino")
        }
        salas.value.forEachIndexed { i, (nombre, manana, tarde) ->
            Tarjeta(radio = 24.dp, relleno = 0.dp) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(nombre, style = Texto.TitleS, color = TintaBase)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ContadorMinimo(Ico.Sun, manana, { v -> salas.value = salas.value.toMutableList().also { it[i] = Triple(nombre, v, tarde) } }, Modifier.weight(1f))
                        ContadorMinimo(Ico.Moon, tarde, { v -> salas.value = salas.value.toMutableList().also { it[i] = Triple(nombre, manana, v) } }, Modifier.weight(1f))
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, TintaTenue, RoundedCornerShape(24.dp))
                .clickable { }
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Plus, tamano = 20.dp)
            Text("Agregar sala", style = Texto.LabelL, color = TintaBase)
        }
    }
}

@Composable
private fun LeyendaTurno(icono: Ico, texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icono(icono, tint = TintaSuave, tamano = 18.dp)
        Text(texto, style = Texto.LabelM, color = TintaSuave)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResAjustesSalasPreview() {
    Access_trackerTheme { ResAjustesSalasScreen({}, {}) }
}
