package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.access_tracker.ui.theme.FondoArenaFuerte
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    icono: Ico? = null,
    esPassword: Boolean = false,
    lineas: Int = 1,
) {
    var verPassword by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = Texto.LabelM, color = TintaSuave)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth(),
            textStyle = Texto.BodyL,
            singleLine = lineas == 1,
            minLines = lineas,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FondoBlanco,
                unfocusedContainerColor = FondoBlanco,
                focusedBorderColor = Petroleo700,
                unfocusedBorderColor = FondoArenaFuerte,
                cursorColor = Petroleo700
            ),
            leadingIcon = if (icono != null) {
                { Icono(icono, tint = TintaSuave, tamano = 20.dp) }
            } else null,
            trailingIcon = if (esPassword) {
                {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icono(Ico.Eye, tint = TintaSuave, tamano = 20.dp)
                    }
                }
            } else null,
            visualTransformation = if (esPassword && !verPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Composable
fun CampoOpciones(
    etiqueta: String,
    valorInicial: String,
    opciones: List<String>,
    modifier: Modifier = Modifier,
    inicio: @Composable () -> Unit = {},
) {
    var abierto by remember { mutableStateOf(false) }
    var elegido by remember { mutableStateOf(valorInicial) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = Texto.LabelM, color = TintaSuave)
        Box {
            CajaCampo(texto = elegido, icono = Ico.Chevdown, onClick = { abierto = true }, inicio = inicio)
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion, style = Texto.BodyL) },
                        onClick = {
                            elegido = opcion
                            abierto = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CampoFecha(etiqueta: String, valorInicial: String, formato: String = "d 'de' MMMM") {
    var fecha by remember { mutableStateOf(valorInicial) }
    var abierto by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = Texto.LabelM, color = TintaSuave)
        CajaCampo(texto = fecha, icono = Ico.Calendar, onClick = { abierto = true })
    }
    if (abierto) {
        DialogoFecha(formato = formato, onCerrar = { abierto = false }, onElegida = { fecha = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoFecha(formato: String, onCerrar: () -> Unit, onElegida: (String) -> Unit) {
    val estado = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(
                onClick = {
                    val milis = estado.selectedDateMillis
                    if (milis != null) {
                        val formatoFecha = SimpleDateFormat(formato, Locale("es", "MX"))
                        formatoFecha.timeZone = TimeZone.getTimeZone("UTC")
                        onElegida(formatoFecha.format(Date(milis)).replaceFirstChar { it.uppercase() })
                    }
                    onCerrar()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = estado)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoHora(etiqueta: String, valorInicial: String) {
    var hora by remember { mutableStateOf(valorInicial) }
    var abierto by remember { mutableStateOf(false) }
    val estado = rememberTimePickerState(initialHour = 7, initialMinute = 0, is24Hour = true)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = Texto.LabelM, color = TintaSuave)
        CajaCampo(texto = hora, icono = Ico.Clock, onClick = { abierto = true })
    }
    if (abierto) {
        AlertDialog(
            onDismissRequest = { abierto = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        hora = "${estado.hour}:${estado.minute.toString().padStart(2, '0')}"
                        abierto = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { abierto = false }) { Text("Cancelar") }
            },
            text = { TimePicker(state = estado) }
        )
    }
}

@Composable
private fun CajaCampo(
    texto: String,
    icono: Ico,
    onClick: () -> Unit,
    inicio: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FondoBlanco)
            .border(1.dp, FondoArenaFuerte, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        inicio()
        Text(texto, style = Texto.BodyL, color = TintaBase, maxLines = 1, modifier = Modifier.weight(1f))
        Icono(icono, tint = TintaSuave, tamano = 20.dp)
    }
}
