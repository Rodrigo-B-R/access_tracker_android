package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.BotonCircular
import com.example.access_tracker.components.CampoFecha
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.ChipOpcion
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TarjetaCifra
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100

@Composable
fun AltaPersonalScreen(onCerrar: () -> Unit, onGuardar: () -> Unit) {
    var nombre by remember { mutableStateOf("Marta Gil Soto") }
    var clave by remember { mutableStateOf("marta.gil") }
    PantallaBase(
        pie = { Boton("Guardar y crear cuenta", onGuardar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Alta de personal", onAtras = onCerrar, cerrar = true, eyebrow = "Personal")
        CampoTexto("Nombre completo", nombre, { nombre = it })
        CampoOpciones(
            "Tipo de personal",
            "Maestra titular",
            listOf("Maestra titular", "Asistente", "Auxiliar", "Cocina", "Intendencia")
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CampoOpciones(
                "Sala",
                "Maternal A",
                listOf("Lactantes", "Maternal A", "Maternal B", "Preescolar"),
                Modifier.weight(1f)
            )
            CampoOpciones("Turno", "Matutino", listOf("Matutino", "Vespertino", "Mixto"), Modifier.weight(1f))
        }
        CampoTexto("Clave de acceso", clave, { clave = it }, icono = Ico.Idcard)
        Nota(
            "Se crea la cuenta con una contraseña temporal. Deberá cambiarla en su primer ingreso.",
            icono = Ico.Lock, fondo = Mostaza100, tint = TintaBase,
        )
    }
}

@Composable
fun PerfilPersonaScreen(onAtras: () -> Unit, onEditar: () -> Unit, onBaja: () -> Unit, onHorario: () -> Unit) {
    PantallaBase(
        separacion = 16.dp,
        pie = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Boton("Editar horario", onHorario, estilo = EstiloBoton.Oscuro)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Boton("Editar datos", onEditar, Modifier.weight(1f), EstiloBoton.Secundario)
                    Boton("Dar de baja", onBaja, Modifier.weight(1f), EstiloBoton.Peligro)
                }
            }
        },
    ) {
        EncabezadoPantalla("Perfil", onAtras = onAtras, accion = { BotonCircular(Ico.Edit, onEditar) })
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp)).background(Petroleo700).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Avatar("AT", fondo = Mostaza500, tinta = TintaBase, lado = 64.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Ana Torres", style = Texto.TitleL, color = FondoCrema)
                Text("Maternal A · Maestra titular", style = Texto.BodyM, color = Petroleo100)
            }
            Chip("Activa", tono = TonoChip.Exito)
        }
        Tarjeta(radio = 24.dp, relleno = 0.dp) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)) {
                DatoPerfil(Ico.Clock, "Turno", "Matutino · 7:00 – 15:00")
                DatoPerfil(Ico.Idcard, "Clave", "ana.torres")
                DatoPerfil(Ico.Calendar, "Alta", "12 ene 2025")
            }
        }
        Eyebrow("Asistencias · septiembre")
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaCifra("18", "Presente", Verde100, Modifier.weight(1f))
            TarjetaCifra("2", "Retardos", Mostaza100, Modifier.weight(1f))
            TarjetaCifra("1", "Falta", Rojo100, Modifier.weight(1f))
        }
        Eyebrow("Historial")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EventoHistorial("Alta en Estancia Cumbres", "12 ene 2025")
            EventoHistorial("Cambio de sala: Lactantes → Maternal A", "3 mar 2026")
        }
    }
}

@Composable
private fun DatoPerfil(icono: Ico, etiqueta: String, valor: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icono(icono, tint = TintaSuave, tamano = 20.dp)
        Text(etiqueta, style = Texto.BodyM, color = TintaSuave, modifier = Modifier.weight(1f))
        Text(valor, style = Texto.LabelM, color = TintaBase)
    }
}

@Composable
private fun EventoHistorial(titulo: String, fecha: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(Petroleo700))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = Texto.LabelM, color = TintaBase)
            Text(fecha, style = Texto.BodyS, color = TintaSuave)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DarDeBajaScreen(onCerrar: () -> Unit, onConfirmar: () -> Unit) {
    var motivo by remember { mutableIntStateOf(0) }
    PantallaBase(
        pie = { Boton("Confirmar baja", onConfirmar, estilo = EstiloBoton.Peligro) },
    ) {
        EncabezadoPantalla("Dar de baja", onAtras = onCerrar, cerrar = true)
        Tarjeta(radio = 24.dp) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar("AT", lado = 48.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Ana Torres", style = Texto.TitleS, color = TintaBase)
                    Text("Maternal A · Maestra titular", style = Texto.BodyS, color = TintaSuave)
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Motivo de baja", style = Texto.LabelM, color = TintaSuave)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Renuncia", "Fin de contrato", "Otro").forEachIndexed { i, t ->
                    ChipOpcion(t, seleccionado = motivo == i, onClick = { motivo = i })
                }
            }
        }
        CampoFecha("Fecha de baja", "Viernes 9 de octubre")
        Nota(
            "La persona sale de la plantilla activa. El historial se conserva y se anonimiza un año después de la baja.",
            icono = Ico.History, fondo = Mostaza100, tint = TintaBase,
        )
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun PerfilPersonaPreview() {
    Access_trackerTheme { PerfilPersonaScreen({}, {}, {}, {}) }
}
