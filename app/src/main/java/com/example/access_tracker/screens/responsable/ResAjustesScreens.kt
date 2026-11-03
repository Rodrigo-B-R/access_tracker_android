package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ContadorMinimo
import com.example.access_tracker.components.Divisor
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.FilaAjuste
import com.example.access_tracker.components.FilaInterruptor
import com.example.access_tracker.components.GrupoFilas
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

/** Menú ⋮ del inicio: bitácora, ajustes y exportar. Se muestra sobre el inicio con un velo. */
@Composable
fun ResMenuScreen(onCerrar: () -> Unit, onBitacora: () -> Unit, onAjustes: () -> Unit, onExportar: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        ResInicioScreen(onAvisos = {}, onMenu = {}, onAtender = {}, onVerAsistencia = {})
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
            OpcionMenu(Ico.Settings, Mostaza100, "Ajustes", "Salas, mínimos y permisos", onAjustes)
            OpcionMenu(Ico.Download, Coral100, "Exportar", "CSV de asistencias", onExportar)
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

/** Configuración de la estancia, permisos y cuenta. */
@Composable
fun ResAjustesScreen(onAtras: () -> Unit, onSalas: () -> Unit, onPermisos: () -> Unit, onCerrarSesion: () -> Unit) {
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Ajustes", onAtras = onAtras)
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Petroleo700).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconoCuadro(Ico.Building, fondo = Mostaza500, lado = 52.dp, radio = 14.dp, tamanoIcono = 24.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Estancia Cumbres", style = Texto.TitleS, color = FondoCrema)
                Text("4 salas · 16 personas · Responsable: María González", style = Texto.BodyS, color = Petroleo100)
            }
        }
        Eyebrow("Configuración de la estancia")
        GrupoFilas {
            FilaAjuste(Ico.Users, Petroleo100, "Salas y mínimos", "Salas de la estancia y personal mínimo por turno", onClick = onSalas)
            Divisor()
            FilaAjuste(Ico.Shield, Mostaza100, "Permisos de la Asistente", "Qué puede ver y reportar tu Asistente", onClick = onPermisos)
            Divisor()
            FilaAjuste(Ico.Lock, Coral100, "Mi cuenta", "Cambiar contraseña y cerrar sesión")
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Rojo100).clickable(onClick = onCerrarSesion).padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Logout, tint = Rojo600)
            Text("Cerrar sesión", style = Texto.LabelL, color = Rojo600, modifier = Modifier.weight(1f))
        }
        Text(
            "Sonrisas de Cristal · versión de prueba",
            style = Texto.BodyS, color = TintaTenue, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
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
        EncabezadoPantalla("Salas y mínimos", onAtras = onAtras, eyebrow = "Ajustes")
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
                .drawBehind {
                    drawRoundRect(
                        color = TintaTenue,
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))),
                    )
                }
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

/** Qué puede ver y reportar la Asistente. Cada cambio queda en la bitácora. */
@Composable
fun ResAjustesPermisosScreen(onAtras: () -> Unit) {
    val activos = remember { mutableStateOf(listOf(true, true, true, false, true)) }
    fun cambiar(i: Int, v: Boolean) { activos.value = activos.value.toMutableList().also { it[i] = v } }
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Permisos de la Asistente", onAtras = onAtras, eyebrow = "Ajustes")
        Tarjeta(radio = 24.dp) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Avatar("SD", fondo = Verde100, tinta = Verde600, lado = 48.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Sofía Díaz", style = Texto.TitleS, color = TintaBase)
                    Text("Asistente · Preescolar", style = Texto.BodyS, color = TintaSuave)
                }
                Chip("${activos.value.count { it }} de 5 activos", tono = TonoChip.Info, punto = false)
            }
        }
        Nota("La Asistente solo reporta y detecta; nunca corrige registros ni da de alta personal.", icono = Ico.Shield)
        GrupoFilas {
            FilaInterruptor(Ico.Users, "Detectar turnos sin cubrir y avisar", activos.value[0], { cambiar(0, it) }, descripcion = "Ve la cobertura por sala")
            Divisor()
            FilaInterruptor(Ico.Clock, "Reportar retardos", activos.value[1], { cambiar(1, it) })
            Divisor()
            FilaInterruptor(Ico.Fingerprint, "Reportar olvidos de checada", activos.value[2], { cambiar(2, it) })
            Divisor()
            FilaInterruptor(Ico.Logout, "Reportar salidas anticipadas", activos.value[3], { cambiar(3, it) })
            Divisor()
            FilaInterruptor(Ico.Alert, "Reportar errores", activos.value[4], { cambiar(4, it) })
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            Icono(Ico.History, tint = TintaSuave, tamano = 16.dp)
            Text("Cada cambio queda en la bitácora de la estancia.", style = Texto.BodyS, color = TintaSuave)
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResAjustesPreview() {
    Access_trackerTheme { ResAjustesScreen({}, {}, {}, {}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResAjustesPermisosPreview() {
    Access_trackerTheme { ResAjustesPermisosScreen({}) }
}
