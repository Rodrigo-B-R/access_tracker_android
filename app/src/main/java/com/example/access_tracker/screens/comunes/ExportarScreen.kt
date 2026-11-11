package com.example.access_tracker.screens.comunes

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.Fechas
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.Estancia
import com.example.access_tracker.screens.responsable.textoAccion
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer

/** Guarda el CSV en la carpeta Descargas (no pide permisos desde Android 10). */
private suspend fun guardarEnDescargas(context: Context, nombre: String, texto: String): Boolean =
    withContext(Dispatchers.IO) {
        val valores = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, nombre)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, valores) ?: return@withContext false
        context.contentResolver.openOutputStream(uri)?.use { it.write(texto.toByteArray(Charsets.UTF_8)); true } ?: false
    }

@Composable
fun ExportarScreen(onAtras: () -> Unit, onDescargar: () -> Unit, direccion: Boolean = false) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    var periodo by remember { mutableIntStateOf(1) }
    var estancias by remember { mutableStateOf<List<Estancia>>(emptyList()) }
    var estancia by remember { mutableIntStateOf(0) }      // 0 = todas
    var trabajando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var exito by remember { mutableStateOf(false) }

    LaunchedEffect(direccion) {
        if (direccion) {
            val r = ServiceLocator.api.get("/daycares", ListSerializer(Estancia.serializer()))
            if (r is ApiResult.Ok) estancias = r.value
        }
    }

    PantallaBase(
        separacion = 18.dp,
        pie = {
            Boton(
                if (trabajando) "Descargando…" else "Descargar CSV",
                {
                    trabajando = true
                    mensaje = null
                    alcance.launch {
                        val hoy = Fechas.hoy()
                        val desde = when (periodo) {
                            0 -> hoy.minusDays(6)
                            1 -> hoy.withDayOfMonth(1)
                            else -> hoy.minusDays(89)
                        }
                        val daycare = if (direccion && estancia > 0) estancias.getOrNull(estancia - 1)?.id else null
                        val extra = daycare?.let { "&daycareId=$it" }.orEmpty()
                        when (val r = ServiceLocator.api.getTexto("/export/attendance?startDate=$desde&endDate=$hoy$extra")) {
                            is ApiResult.Ok -> {
                                val ok = guardarEnDescargas(contexto, "asistencias_${desde}_$hoy.csv", r.value)
                                exito = ok
                                mensaje = if (ok) "Listo: guardado en Descargas como asistencias_${desde}_$hoy.csv"
                                else "No se pudo guardar el archivo."
                            }
                            is ApiResult.Error -> { exito = false; mensaje = r.error.textoAccion() }
                        }
                        trabajando = false
                    }
                },
                estilo = EstiloBoton.Oscuro,
                habilitado = !trabajando,
            )
        },
    ) {
        EncabezadoPantalla("Exportar", onAtras = onAtras, eyebrow = if (direccion) "Dirección" else "Mi estancia")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Periodo", style = Texto.LabelM, color = TintaSuave)
            Segmentado(listOf("7 días", "Este mes", "90 días"), periodo, { periodo = it })
        }
        if (direccion && estancias.isNotEmpty()) {
            CampoOpciones(
                "Estancia", "Todas las estancias", listOf("Todas las estancias") + estancias.map { it.name },
                inicio = { Icono(Ico.Building, tint = TintaSuave, tamano = 20.dp) },
                onElegido = { estancia = it },
            )
        }
        mensaje?.let { Nota(it, icono = if (exito) Ico.Check else Ico.Alert, fondo = if (exito) FondoArena else Rojo100, tint = TintaBase) }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(FondoArena)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconoCuadro(Ico.File, fondo = FondoBlanco, lado = 40.dp, radio = 14.dp, tamanoIcono = 24.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Formato CSV", style = Texto.LabelL, color = TintaBase)
                Text("Una fila por persona y día, con entrada, salida y estado. Se guarda en Descargas.", style = Texto.BodyS, color = TintaSuave)
            }
        }
    }
}
