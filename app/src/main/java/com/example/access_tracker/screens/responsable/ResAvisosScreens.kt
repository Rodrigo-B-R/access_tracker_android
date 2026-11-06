package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.AvisoItem
import com.example.access_tracker.components.AvisoUrgente
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.BotonFlotante
import com.example.access_tracker.components.BarraSegmentos
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EncabezadoSeccion
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Interruptor
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

/** Avisos internos (extra): la Responsable envía y recibe de Dirección. */
@Composable
fun ResAvisosScreen(onAtras: () -> Unit, onNuevo: () -> Unit, onEstadoLectura: () -> Unit) {
    var filtro by remember { mutableIntStateOf(0) }
    PantallaBase(
        separacion = 16.dp,
        botonFlotante = { BotonFlotante("Nuevo aviso", onNuevo, Modifier.padding(end = 8.dp, bottom = 32.dp)) },
    ) {
        EncabezadoPantalla("Avisos", onAtras = onAtras, grande = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Recibidos", "Enviados").forEachIndexed { i, t ->
                FiltroPill(t, activo = filtro == i, onClick = { filtro = i })
            }
        }
        AvisoUrgente(titulo = "Cierre anticipado por lluvia", hace = "Enviado · hoy 10:12", onClick = onEstadoLectura) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BarraSegmentos(14, 9, colorLleno = FondoBlanco, colorVacio = Rojo100.copy(alpha = 0.6f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("9 de 14 confirmaron", style = Texto.LabelM, color = FondoBlanco)
                    Text("Ver estado", style = Texto.LabelM, color = FondoBlanco)
                }
            }
        }
        AvisoItem("Cambio de horario", TonoChip.Alerta, "ayer", "Nuevo horario de comida desde el lunes", fondo = FondoBlanco) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(Ico.Check, tint = Verde600, tamano = 16.dp)
                Text("14 de 14 confirmaron", style = Texto.BodyS, color = Verde600)
            }
        }
        AvisoItem("Reunión", TonoChip.Info, "lun 5 oct", "Reunión de personal el viernes a las 9:00", fondo = FondoBlanco) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(Ico.Check, tint = Verde600, tamano = 16.dp)
                Text("Lectura confirmada", style = Texto.BodyS, color = Verde600)
            }
        }
    }
}

/** Redacta un aviso: tipo, destinatarios y confirmación de lectura. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResNuevoAvisoScreen(onCerrar: () -> Unit, onEnviar: () -> Unit) {
    var titulo by remember { mutableStateOf("Cierre anticipado por lluvia") }
    var mensaje by remember { mutableStateOf("Hoy cerramos a las 13:00. Entreguen a las niñas y niños desde las 12:30.") }
    var tipo by remember { mutableIntStateOf(3) }
    var destino by remember { mutableIntStateOf(0) }
    var confirmar by remember { mutableStateOf(true) }
    PantallaBase(
        separacion = 16.dp,
        pie = { Boton("Enviar aviso", onEnviar) },
    ) {
        EncabezadoPantalla("Nuevo aviso", onAtras = onCerrar, cerrar = true)
        CampoTexto("Título", titulo, { titulo = it })
        CampoTexto("Mensaje", mensaje, { mensaje = it }, lineas = 3)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tipo", style = Texto.LabelM, color = TintaSuave)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Cambio de horario", "Reunión", "Recordatorio", "Urgente").forEachIndexed { i, t ->
                    ChipOpcion(t, seleccionado = tipo == i, onClick = { tipo = i })
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Destinatarios", style = Texto.LabelM, color = TintaSuave)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todo el personal", "Un turno", "Una sala", "Personas").forEachIndexed { i, t ->
                    ChipOpcion(t, seleccionado = destino == i, onClick = { destino = i })
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(FondoBlanco).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Pedir confirmación de lectura", style = Texto.LabelL, color = TintaBase)
                Text("Verás quién lo leyó y a qué hora", style = Texto.BodyS, color = TintaSuave)
            }
            Interruptor(confirmar, { confirmar = it })
        }
    }
}

/** Quién ya confirmó y quién falta, con recordatorio a los pendientes. */
@Composable
fun ResEstadoLecturaScreen(onAtras: () -> Unit, onRecordatorio: () -> Unit) {
    PantallaBase(
        separacion = 16.dp,
        pie = { Boton("Enviar recordatorio a los 5", onRecordatorio) },
    ) {
        EncabezadoPantalla("Estado de lectura", onAtras = onAtras)
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Petroleo700).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Chip("Urgente", tono = TonoChip.Peligro)
                Text("Hoy 10:12", style = Texto.BodyS, color = Petroleo100)
            }
            Text("Cierre anticipado por lluvia", style = Texto.TitleM, color = FondoCrema)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
                Text("9", style = Texto.DisplayL, color = Mostaza500)
                Text("de 14 confirmaron", style = Texto.LabelL, color = Petroleo100)
            }
            BarraSegmentos(14, 9, colorLleno = Mostaza500, colorVacio = Petroleo500)
        }
        EncabezadoSeccion("Sin confirmar · 5")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilaPersona("SD", "Sofía Díaz", "Preescolar", fondoAvatar = Verde100, tintaAvatar = Verde600, ladoAvatar = 40.dp) { Chip("Sin leer", tono = TonoChip.Peligro) }
            FilaPersona("PR", "Paola Reyes", "Maternal A", ladoAvatar = 40.dp) { Chip("Sin leer", tono = TonoChip.Peligro) }
            FilaPersona("RM", "Rosa Medina", "Cocina", fondoAvatar = Mostaza100, tintaAvatar = TintaBase, ladoAvatar = 40.dp) { Chip("Sin leer", tono = TonoChip.Peligro) }
        }
        EncabezadoSeccion("Confirmaron · 9")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilaPersona("AT", "Ana Torres", "Maternal A", ladoAvatar = 40.dp) { HoraConfirmada("10:14") }
            FilaPersona("LH", "Lucía Herrera", "Lactantes", fondoAvatar = Mostaza100, tintaAvatar = TintaBase, ladoAvatar = 40.dp) { HoraConfirmada("10:15") }
        }
    }
}

@Composable
private fun HoraConfirmada(hora: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icono(Ico.Check, tint = Verde600, tamano = 16.dp)
        Text(hora, style = Texto.LabelM, color = Verde600)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResAvisosPreview() {
    Access_trackerTheme { ResAvisosScreen({}, {}, {}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResEstadoLecturaPreview() {
    Access_trackerTheme { ResEstadoLecturaScreen({}, {}) }
}
