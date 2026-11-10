package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.AccionAviso
import com.example.access_tracker.components.AvisoItem
import com.example.access_tracker.components.AvisoUrgente
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.EtiquetaUrgente
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.LecturaConfirmada
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.SinLeer
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

/** Un aviso tal como se pinta en la bandeja. */
data class AvisoVista(
    val id: Long,
    val etiqueta: String,
    val tono: TonoChip,
    val urgente: Boolean,
    /** Texto de la esquina del aviso (la API no manda fecha: se muestra quien lo envia). */
    val de: String,
    val titulo: String,
    val texto: String,
    val leido: Boolean,
    val requiereConfirmacion: Boolean,
)

/** Version de demostracion (indice de pantallas): datos fijos. */
@Composable
fun AvisosBandejaScreen(onAtras: () -> Unit, onAbrirUrgente: () -> Unit) {
    AvisosBandejaContenido(
        avisos = listOf(
            AvisoVista(1, "Urgente", TonoChip.Peligro, true, "Hace 10 min", "Cierre anticipado por lluvia",
                "Se recoge a las niñas y niños a las 13:00. Avisa a las familias.", leido = false, requiereConfirmacion = true),
            AvisoVista(2, "Cambio de horario", TonoChip.Alerta, false, "Ayer", "Nuevo horario de comida desde el lunes",
                "", leido = false, requiereConfirmacion = false),
            AvisoVista(3, "Reunión", TonoChip.Info, false, "Lun 5 oct", "Reunión de personal el viernes a las 9:00",
                "", leido = true, requiereConfirmacion = false),
            AvisoVista(4, "Recordatorio", TonoChip.Neutro, false, "Vie 2 oct", "Entregar las bitácoras al cierre del turno",
                "", leido = true, requiereConfirmacion = false),
        ),
        onAtras = onAtras,
        onAbrir = { onAbrirUrgente() },
    )
}

@Composable
fun AvisosBandejaContenido(
    avisos: List<AvisoVista>,
    onAtras: () -> Unit,
    onAbrir: (Long) -> Unit,
    mensaje: String? = null,
) {
    // Los urgentes sin leer van destacados arriba; el resto, en "Anteriores".
    val destacados = avisos.filter { it.urgente && !it.leido }
    val resto = avisos.filterNot { it in destacados }
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Avisos", onAtras = onAtras, grande = true)
        if (mensaje != null) Text(mensaje, style = Texto.BodyM, color = TintaSuave)
        destacados.forEach { a ->
            AvisoUrgente(
                titulo = a.titulo,
                texto = a.texto.ifBlank { null },
                hace = a.de,
                onClick = { onAbrir(a.id) },
            ) {
                AccionAviso(
                    if (a.requiereConfirmacion) "Pide confirmar lectura" else "Abrir aviso",
                    Ico.Eye,
                    onClick = { onAbrir(a.id) },
                )
            }
        }
        if (resto.isNotEmpty()) Eyebrow(if (destacados.isEmpty()) "Avisos" else "Anteriores")
        resto.forEach { a ->
            AvisoItem(
                a.etiqueta, a.tono, a.de, a.titulo,
                sinLeer = !a.leido,
                onClick = { onAbrir(a.id) },
            ) {
                if (a.leido) LecturaConfirmada() else SinLeer()
            }
        }
    }
}

/** Version de demostracion (indice de pantallas): datos fijos. */
@Composable
fun AvisoDetalleScreen(onAtras: () -> Unit, onConfirmar: () -> Unit) {
    AvisoDetalleContenido(
        etiqueta = "Urgente", urgente = true, titulo = "Cierre anticipado por lluvia",
        autor = "Marisol García",
        mensaje = "Por la alerta de lluvia fuerte, hoy cerramos a las 13:00. Las niñas y niños se entregan a partir de las 12:30.\n\n" +
            "Avisen a las familias y revisen que no quede nadie en las salas antes de salir.",
        requiereConfirmacion = true, leido = false, enviando = false,
        onAtras = onAtras, onConfirmar = onConfirmar,
    )
}

@Composable
fun AvisoDetalleContenido(
    etiqueta: String,
    urgente: Boolean,
    titulo: String,
    autor: String,
    mensaje: String,
    requiereConfirmacion: Boolean,
    leido: Boolean,
    enviando: Boolean,
    onAtras: () -> Unit,
    onConfirmar: () -> Unit,
    error: String? = null,
) {
    val colorCabecera: Color = if (urgente) Rojo600 else Petroleo700
    val iniciales = autor.split(' ').filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
    PantallaBase(
        separacion = 16.dp,
        cabecera = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(colorCabecera)
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                EncabezadoPantalla("Aviso", onAtras = onAtras, fondoBoton = FondoBlanco, tinta = FondoBlanco)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (urgente) EtiquetaUrgente(relleno = 12)
                    else Text(etiqueta, style = Texto.BodyM, color = FondoBlanco)
                }
                Text(titulo, style = Texto.DisplayL, color = FondoBlanco)
                Row(
                    Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(FondoBlanco),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iniciales, style = Texto.LabelS, color = colorCabecera)
                    }
                    Text("De: $autor", style = Texto.LabelM, color = FondoBlanco)
                }
            }
        },
        pie = {
            if (error != null) Text(error, style = Texto.BodyM, color = Coral500)
            when {
                requiereConfirmacion && !leido -> Boton(
                    if (enviando) "Confirmando…" else "Confirmar lectura",
                    onConfirmar, estilo = EstiloBoton.Oscuro, habilitado = !enviando,
                )
                requiereConfirmacion -> Boton(
                    "Lectura confirmada", {}, estilo = EstiloBoton.Secundario, habilitado = false,
                )
                else -> Boton("Cerrar", onAtras, estilo = EstiloBoton.Oscuro)
            }
        },
    ) {
        Text(mensaje, style = Texto.BodyL, color = TintaBase)
        if (requiereConfirmacion) {
            Nota(
                "Tu responsable ve quién ya confirmó la lectura.",
                icono = Ico.Eye,
                fondo = FondoArena,
                tint = TintaSuave,
                colorTexto = TintaSuave
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun AvisosBandejaPreview() {
    Access_trackerTheme { AvisosBandejaScreen({}, {}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun AvisoDetallePreview() {
    Access_trackerTheme { AvisoDetalleScreen({}, {}) }
}
