package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.access_tracker.components.Avatar
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.Casilla
import com.example.access_tracker.components.CampoFecha
import com.example.access_tracker.components.CampoHora
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.CuadroPersona
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.FilaPersona
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoArena
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

private data class PersonaTurno(val iniciales: String, val nombre: String, val sala: String, val fondo: androidx.compose.ui.graphics.Color, val tinta: androidx.compose.ui.graphics.Color)

private val PersonasTurno = listOf(
    PersonaTurno("AT", "Ana Torres", "Maternal A", Petroleo100, Petroleo700),
    PersonaTurno("LH", "Lucía Herrera", "Lactantes", Mostaza100, TintaBase),
    PersonaTurno("CR", "Carmen Ruiz", "Maternal B", Coral100, TintaBase),
    PersonaTurno("SD", "Sofía Díaz", "Preescolar", Verde100, Verde600),
    PersonaTurno("RM", "Rosa Medina", "Cocina", Mostaza100, TintaBase),
)

/** Registro de la entrada de todo un turno con una sola acción. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistroTurnoScreen(onCerrar: () -> Unit, onRegistrar: () -> Unit) {
    var matutino by remember { mutableStateOf(true) }
    val marcados = remember { mutableStateListOf(true, true, true, true, true) }
    val todos = marcados.all { it }
    val cantidad = marcados.count { it }

    PantallaBase(
        pie = { Boton("Registrar entrada de $cantidad personas", onRegistrar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Entrada de turno completo", onAtras = onCerrar, cerrar = true, eyebrow = "Asistencia · hoy")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Turno", style = Texto.LabelM, color = TintaSuave)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipOpcion("Matutino", Modifier.weight(1f), seleccionado = matutino, onClick = { matutino = true })
                ChipOpcion("Vespertino", Modifier.weight(1f), seleccionado = !matutino, onClick = { matutino = false })
            }
        }
        CampoHora("Hora de entrada", "7:00 a. m.")
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Petroleo100).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Seleccionar todos", style = Texto.LabelL, color = Petroleo700, modifier = Modifier.weight(1f))
            Text("$cantidad de ${PersonasTurno.size}", style = Texto.LabelM, color = Petroleo700)
            Casilla(todos, onClick = { val nuevo = !todos; for (i in marcados.indices) marcados[i] = nuevo })
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PersonasTurno.forEachIndexed { i, p ->
                FilaPersona(p.iniciales, p.nombre, p.sala, fondoAvatar = p.fondo, tintaAvatar = p.tinta, ladoAvatar = 40.dp) {
                    Casilla(marcados[i], onClick = { marcados[i] = !marcados[i] })
                }
            }
            FilaPersona("PR", "Paola Reyes", "Maternal A · ya registrada 6:52", ladoAvatar = 40.dp, fondo = FondoArena) {
                Casilla(marcada = true, bloqueada = true)
            }
        }
    }
}

/** Ausencia o incidencia de una persona (el retardo se calcula con la checada), con motivo en categoría general. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrarAusenciaScreen(onCerrar: () -> Unit, onGuardar: () -> Unit, persona: String = "Paola Reyes") {
    var tipo by remember { mutableIntStateOf(0) }
    var motivo by remember { mutableIntStateOf(0) }
    PantallaBase(
        separacion = 20.dp,
        pie = { Boton("Guardar registro", onGuardar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Registrar ausencia o incidencia", onAtras = onCerrar, cerrar = true)
        CampoOpciones(
            "Persona", persona, listOf("Ana Torres", "Lucía Herrera", "Carmen Ruiz", "Sofía Díaz", "Paola Reyes", "Rosa Medina"),
            inicio = { Avatar(persona.split(" ").take(2).joinToString("") { it.take(1) }.uppercase(), lado = 28.dp) },
        )
        CampoFecha("Fecha", "Miércoles 7 de octubre")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tipo", style = Texto.LabelM, color = TintaSuave)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Falta", "Incidencia").forEachIndexed { i, t ->
                    ChipOpcion(t, Modifier.weight(1f), seleccionado = tipo == i, onClick = { tipo = i })
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Motivo (categoría general)", style = Texto.LabelM, color = TintaSuave)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Enfermedad", "Permiso", "Personal", "Transporte", "Otro").forEachIndexed { i, t ->
                    ChipOpcion(t, seleccionado = motivo == i, onClick = { motivo = i })
                }
            }
        }
        Nota("Solo la Responsable ve los motivos. No captures diagnósticos ni datos de niñas y niños.", icono = Ico.Shield)
    }
}


@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun RegistroTurnoPreview() {
    Access_trackerTheme { RegistroTurnoScreen({}, {}) }
}
