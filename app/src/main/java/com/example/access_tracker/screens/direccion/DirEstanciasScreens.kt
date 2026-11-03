package com.example.access_tracker.screens.direccion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.BotonFlotante
import com.example.access_tracker.components.CampoTexto
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Divisor
import com.example.access_tracker.components.Flecha
import com.example.access_tracker.components.GrupoFilas
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.SalaMini
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoArenaFuerte
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

/** Resumen de Dirección (solo lectura): cuántas estancias están completas y cuáles tienen faltante. */
@Composable
fun DirResumenScreen(onEstancia: () -> Unit) {
    PantallaBase(
        conBarra = true,
        separacion = 18.dp,
        cabecera = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(Petroleo700)
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Dirección", style = Texto.LabelM, color = Petroleo100)
                        Text("Resumen", style = Texto.DisplayL, color = FondoCrema)
                    }
                    Row(
                        Modifier.clip(RoundedCornerShape(24.dp)).background(Petroleo500).padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icono(Ico.Calendar, tint = FondoCrema, tamano = 20.dp)
                        Text("Mié 7 oct", style = Texto.LabelL, color = FondoCrema)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Cifra("3", "estancias\ncompletas", FondoBlanco, Modifier.weight(1f))
                    Cifra("2", "con\nfaltante", Coral500, Modifier.weight(1f))
                }
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EstanciaEstado("Apodaca", "Falta 1 en Maternal A", completa = false, Modifier.weight(1f), onEstancia)
                EstanciaEstado("Escobedo", "Faltan 2 en Lactantes", completa = false, Modifier.weight(1f), onEstancia)
            }
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EstanciaEstado("Cumbres", "Completa", completa = true, Modifier.weight(1f), onEstancia)
                EstanciaEstado("San Nicolás", "Completa", completa = true, Modifier.weight(1f), onEstancia)
            }
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EstanciaEstado("Guadalupe", "Completa", completa = true, Modifier.weight(1f), onEstancia)
                Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Cifra(valor: String, etiqueta: String, fondo: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(28.dp)).background(fondo).padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(valor, style = Texto.DisplayL, color = TintaBase)
        Text(etiqueta, style = Texto.LabelL, color = TintaBase)
    }
}

@Composable
private fun EstanciaEstado(nombre: String, estado: String, completa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val forma = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(if (completa) Verde100 else Rojo100)
            .then(if (!completa) Modifier.border(BorderStroke(2.dp, Rojo600), forma) else Modifier)
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombre, style = Texto.TitleS, color = TintaBase)
            if (completa) Box(Modifier.size(22.dp).clip(CircleShape).background(Verde600))
            else Box(Modifier.size(22.dp).clip(RoundedCornerShape(5.dp)).background(Rojo600))
        }
        Text(estado, style = Texto.LabelM, color = if (completa) Verde600 else Rojo600)
    }
}

private data class DatosEstancia(val nombre: String, val resumen: String, val responsable: String)

private val Estancias = listOf(
    DatosEstancia("Estancia Cumbres", "4 salas · 16 personas", "María González"),
    DatosEstancia("Estancia San Nicolás", "3 salas · 12 personas", "Laura Pérez"),
    DatosEstancia("Estancia Apodaca", "4 salas · 15 personas", "Elena Rivas"),
    DatosEstancia("Estancia Guadalupe", "3 salas · 11 personas", "Julia Salinas"),
    DatosEstancia("Estancia Escobedo", "2 salas · 8 personas", "Irma Castillo"),
)

/** Lista de las estancias de la fundación. */
@Composable
fun DirEstanciasScreen(onEstancia: () -> Unit, onAlta: () -> Unit) {
    PantallaBase(
        conBarra = true,
        separacion = 14.dp,
        overlay = {
            BotonFlotante("Dar de alta", onAlta, Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 115.dp))
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Eyebrow("Dirección")
            TituloPantalla("Estancias", subtitulo = "5 estancias de la fundación")
        }
        Estancias.forEach { e ->
            Tarjeta(radio = 24.dp, relleno = 0.dp, onClick = onEstancia) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconoCuadro(Ico.Building, fondo = Petroleo100, tint = Petroleo700, lado = 48.dp, radio = 14.dp, tamanoIcono = 24.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(e.nombre, style = Texto.TitleS, color = TintaBase)
                        Text(e.resumen, style = Texto.BodyS, color = TintaSuave)
                        Text("Responsable: ${e.responsable}", style = Texto.BodyS, color = TintaSuave)
                    }
                    Flecha()
                }
            }
        }
    }
}

/** Detalle de una estancia: cobertura por sala del día. Solo lectura. */
@Composable
fun DirDetalleEstanciaScreen(onAtras: () -> Unit, onEstadisticas: () -> Unit, onBitacora: () -> Unit) {
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla(
            "Estancia Apodaca", onAtras = onAtras, eyebrow = "Miércoles 7 de octubre",
            accion = { EtiquetaLectura("Lectura") },
        )
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Rojo600).padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Alert, tint = FondoBlanco, tamano = 26.dp)
            Text("Falta 1 persona en Maternal A", style = Texto.TitleS, color = FondoBlanco, modifier = Modifier.weight(1f))
        }
        Eyebrow("Cobertura por sala")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SalaMini("Lactantes", 4, 4, Modifier.weight(1f))
                SalaMini("Maternal A", 2, 3, Modifier.weight(1f), bajoMinimo = true)
            }
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SalaMini("Maternal B", 3, 3, Modifier.weight(1f))
                SalaMini("Preescolar", 5, 5, Modifier.weight(1f))
            }
        }
        Eyebrow("Personal insuficiente del día")
        Tarjeta(radio = 24.dp) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                IconoCuadro(Ico.Clock, fondo = Coral100, lado = 44.dp, radio = 14.dp, tamanoIcono = 24.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("1 turno sin cubrir", style = Texto.LabelL, color = TintaBase)
                    Text("Maternal A · 7:00 – 15:00", style = Texto.BodyS, color = TintaSuave)
                }
            }
        }
        GrupoFilas {
            FilaEnlace(Ico.Chart, "Ver estadísticas de esta estancia", onEstadisticas)
            Divisor()
            FilaEnlace(Ico.History, "Ver bitácora de esta estancia", onBitacora)
        }
    }
}

@Composable
private fun FilaEnlace(icono: Ico, texto: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = Petroleo700, tamano = 22.dp)
        Text(texto, style = Texto.LabelL, color = TintaBase, modifier = Modifier.weight(1f))
        Flecha()
    }
}

/** Alta de una estancia nueva con sus salas y la cuenta de su Responsable. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirAltaEstanciaScreen(onCerrar: () -> Unit, onGuardar: () -> Unit) {
    val nombre = remember { androidx.compose.runtime.mutableStateOf("Estancia Juárez") }
    val responsable = remember { androidx.compose.runtime.mutableStateOf("Patricia Ramos") }
    val clave = remember { androidx.compose.runtime.mutableStateOf("patricia.ramos") }
    val salas = remember { mutableStateListOf("Lactantes", "Maternal A", "Maternal B", "Preescolar") }

    PantallaBase(
        pie = { Boton("Dar de alta estancia", onGuardar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Dar de alta estancia", onAtras = onCerrar, cerrar = true)
        CampoTexto("Nombre de la estancia", nombre.value, { nombre.value = it }, icono = Ico.Building, activo = true)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Salas", style = Texto.LabelM, color = TintaSuave)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                salas.toList().forEach { sala ->
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(FondoBlanco)
                            .border(BorderStroke(1.dp, FondoArenaFuerte), RoundedCornerShape(22.dp))
                            .padding(start = 16.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(sala, style = Texto.LabelM, color = TintaBase)
                        Icono(Ico.X, tint = TintaSuave, tamano = 16.dp, modifier = Modifier.clickable { salas.remove(sala) })
                    }
                }
                Row(
                    Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = TintaTenue,
                                cornerRadius = CornerRadius(22.dp.toPx()),
                                style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))),
                            )
                        }
                        .clickable { salas.add("Sala ${salas.size + 1}") }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icono(Ico.Plus, tamano = 16.dp)
                    Text("Agregar sala", style = Texto.LabelM, color = TintaBase)
                }
            }
        }
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(FondoArena).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icono(Ico.Userplus, tamano = 20.dp)
                Text("Cuenta de la Responsable", style = Texto.TitleS, color = TintaBase)
            }
            CampoTexto("Nombre", responsable.value, { responsable.value = it })
            CampoTexto("Clave de acceso", clave.value, { clave.value = it }, icono = Ico.Idcard)
        }
        Nota(
            "Se crea la cuenta con una contraseña temporal. La Responsable define después los mínimos por sala y da de alta a su personal.",
            icono = Ico.Lock, fondo = Mostaza100, tint = TintaBase,
        )
    }
}

@Composable
internal fun EtiquetaLectura(texto: String) {
    Row(
        Modifier.clip(RoundedCornerShape(20.dp)).background(FondoArena).padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(Ico.Eye, tint = TintaSuave, tamano = 16.dp)
        Text(texto, style = Texto.LabelS, color = TintaSuave)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun DirResumenPreview() {
    Access_trackerTheme { DirResumenScreen({}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun DirEstanciasPreview() {
    Access_trackerTheme { DirEstanciasScreen({}, {}) }
}
