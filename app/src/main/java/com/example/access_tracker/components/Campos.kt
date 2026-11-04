package com.example.access_tracker.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoArenaFuerte
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

private val FormaCampo = RoundedCornerShape(18.dp)

/** Campo de texto del diseño: etiqueta arriba, caja blanca con borde arena (petróleo si está activo). */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    icono: Ico? = null,
    esPassword: Boolean = false,
    visible: Boolean = false,
    onAlternarVisible: (() -> Unit)? = null,
    activo: Boolean = false,
    placeholder: String = "",
    lineas: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = Texto.LabelM, color = TintaSuave)
        CajaCampo(activo) {
            if (icono != null) Icono(icono, tint = TintaSuave, tamano = 20.dp)
            BasicTextField(
                value = valor,
                onValueChange = onCambio,
                modifier = Modifier.weight(1f),
                singleLine = lineas == 1,
                minLines = lineas,
                textStyle = Texto.BodyL.copy(color = TintaBase),
                cursorBrush = SolidColor(Petroleo700),
                visualTransformation = if (esPassword && !visible) PasswordVisualTransformation('•') else VisualTransformation.None,
                keyboardOptions = keyboardOptions,
                decorationBox = { interno ->
                    if (valor.isEmpty() && placeholder.isNotEmpty()) Text(placeholder, style = Texto.BodyL, color = TintaTenue)
                    interno()
                },
            )
            if (esPassword) {
                Icono(
                    Ico.Eye,
                    tint = TintaSuave,
                    tamano = 20.dp,
                    modifier = Modifier.clickable { onAlternarVisible?.invoke() },
                )
            }
        }
    }
}

/**
 * Campo que se ve igual pero abre algo al tocarlo. Con [opciones] despliega un menú; con el icono de calendario
 * abre un selector de fecha y con el de reloj uno de hora. Conserva el formato del texto de ejemplo recibido.
 */
@Composable
fun CampoSelector(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    icono: Ico? = null,
    inicio: (@Composable () -> Unit)? = null,
    activo: Boolean = false,
    opciones: List<String> = emptyList(),
    onSeleccion: (String) -> Unit = {},
    onClick: () -> Unit = {},
) {
    var abierto by remember { mutableStateOf(false) }
    var dialogo by remember { mutableStateOf(false) }
    var elegido by remember(valor) { mutableStateOf(valor) }
    val alTocar: () -> Unit = when {
        icono == Ico.Calendar || icono == Ico.Clock -> { { dialogo = true } }
        opciones.isNotEmpty() -> { { abierto = true } }
        else -> onClick
    }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = Texto.LabelM, color = TintaSuave)
        Box {
            CajaCampo(activo = activo || abierto, modifier = Modifier.clickable(onClick = alTocar)) {
                inicio?.invoke()
                Text(elegido, style = Texto.BodyL, color = TintaBase, modifier = Modifier.weight(1f), maxLines = 1)
                if (icono != null) Icono(icono, tint = TintaSuave, tamano = 20.dp)
            }
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }, containerColor = FondoBlanco) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion, style = Texto.BodyL, color = TintaBase) },
                        onClick = { elegido = opcion; abierto = false; onSeleccion(opcion) },
                    )
                }
            }
        }
    }
    if (dialogo) {
        val alElegir = { nuevo: String -> elegido = nuevo; onSeleccion(nuevo) }
        if (icono == Ico.Calendar) DialogoFecha(elegido, { dialogo = false }, alElegir)
        else DialogoHora(elegido, { dialogo = false }, alElegir)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DialogoFecha(actual: String, onCerrar: () -> Unit, onElegida: (String) -> Unit) {
    val estado = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(onClick = { estado.selectedDateMillis?.let { onElegida(formatoFecha(it, actual)) }; onCerrar() }) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } },
        colors = DatePickerDefaults.colors(containerColor = FondoBlanco),
    ) {
        DatePicker(
            estado,
            colors = DatePickerDefaults.colors(
                containerColor = FondoBlanco,
                selectedDayContainerColor = Petroleo700,
                selectedDayContentColor = FondoCrema,
                todayDateBorderColor = Petroleo700,
                todayContentColor = Petroleo700,
                selectedYearContainerColor = Petroleo700,
                selectedYearContentColor = FondoCrema,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoHora(actual: String, onCerrar: () -> Unit, onElegida: (String) -> Unit) {
    val (h, m) = horaDe(actual)
    val estado = rememberTimePickerState(initialHour = h, initialMinute = m, is24Hour = false)
    Dialog(onDismissRequest = onCerrar) {
        Surface(shape = RoundedCornerShape(28.dp), color = FondoBlanco) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                TimePicker(
                    estado,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = FondoArena,
                        selectorColor = Petroleo700,
                        clockDialSelectedContentColor = FondoCrema,
                        clockDialUnselectedContentColor = TintaBase,
                        periodSelectorBorderColor = FondoArenaFuerte,
                        periodSelectorSelectedContainerColor = Mostaza100,
                        periodSelectorUnselectedContainerColor = FondoBlanco,
                        periodSelectorSelectedContentColor = TintaBase,
                        periodSelectorUnselectedContentColor = TintaSuave,
                        timeSelectorSelectedContainerColor = Petroleo100,
                        timeSelectorUnselectedContainerColor = FondoArena,
                        timeSelectorSelectedContentColor = Petroleo700,
                        timeSelectorUnselectedContentColor = TintaBase,
                    ),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onCerrar) { Text("Cancelar") }
                    Box(Modifier.width(8.dp))
                    TextButton(onClick = { onElegida(formatoHora(estado.hour, estado.minute)); onCerrar() }) { Text("Aceptar") }
                }
            }
        }
    }
}

private val Mexico = Locale.forLanguageTag("es-MX")

/** Da formato a la fecha elegida con el mismo estilo del texto de ejemplo: «Septiembre 2026», «Martes 6 de octubre» o «Mié 7 oct». */
internal fun formatoFecha(milis: Long, plantilla: String): String {
    val fecha = Instant.ofEpochMilli(milis).atZone(ZoneOffset.UTC).toLocalDate()
    val patron = when {
        Regex("""\S+ \d{4}""").matches(plantilla) -> "MMMM yyyy"
        " de " in plantilla -> "EEEE d 'de' MMMM"
        else -> "EEE d MMM"
    }
    return fecha.format(DateTimeFormatter.ofPattern(patron, Mexico)).replace(".", "").replaceFirstChar { it.uppercase() }
}

/** Hora y minuto del texto «7:25 a. m.»; si no se entiende, 7:00. */
private fun horaDe(texto: String): Pair<Int, Int> {
    val m = Regex("""(\d{1,2}):(\d{2})\s*([ap])""", RegexOption.IGNORE_CASE).find(texto) ?: return 7 to 0
    val hora12 = m.groupValues[1].toInt() % 12
    val pm = m.groupValues[3].equals("p", ignoreCase = true)
    return (if (pm) hora12 + 12 else hora12) to m.groupValues[2].toInt()
}

private fun formatoHora(hora: Int, minuto: Int): String {
    val h12 = if (hora % 12 == 0) 12 else hora % 12
    return "%d:%02d %s".format(h12, minuto, if (hora < 12) "a. m." else "p. m.")
}

@Composable
private fun CajaCampo(
    activo: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(FormaCampo)
            .background(FondoBlanco)
            .border(if (activo) BorderStroke(2.dp, Petroleo700) else BorderStroke(1.dp, FondoArenaFuerte), FormaCampo)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = contenido,
    )
}
