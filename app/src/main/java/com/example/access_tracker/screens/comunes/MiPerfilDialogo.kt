package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.access_tracker.data.Carga
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.aCarga
import com.example.access_tracker.model.MiPerfil
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

/** Nombre del rol de la API ("employee"...) en espanol. */
fun nombreRol(rol: String?): String = when (rol) {
    "employee" -> "Empleada"
    "manager" -> "Responsable de estancia"
    "director" -> "Dirección"
    else -> rol.orEmpty()
}

/** "Mi perfil": carga GET /profile y lo muestra en un dialogo. Autocontenido: no necesita navegacion. */
@Composable
fun MiPerfilDialogo(onCerrar: () -> Unit) {
    var intento by remember { mutableStateOf(0) }
    var estado by remember { mutableStateOf<Carga<MiPerfil>>(Carga.Cargando) }
    LaunchedEffect(intento) {
        estado = Carga.Cargando
        estado = ServiceLocator.api.get("/profile", MiPerfil.serializer()).aCarga()
    }
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = FondoBlanco,
        title = { Text("Mi perfil", style = Texto.TitleL, color = TintaBase) },
        text = {
            when (val e = estado) {
                Carga.Cargando -> Text("Cargando…", style = Texto.BodyM, color = TintaSuave)
                is Carga.Error -> Text(e.mensaje, style = Texto.BodyM, color = TintaBase)
                is Carga.Listo -> {
                    val p = e.datos
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(p.name, style = Texto.TitleL, color = TintaBase)
                        Dato("Rol", nombreRol(p.role?.name))
                        Dato("Puesto", p.position?.name)
                        Dato("Estancia", p.daycare?.name)
                        Dato("Clave de acceso", p.username)
                        Dato("Correo", p.email)
                        Dato("Teléfono", p.phone)
                    }
                }
            }
        },
        confirmButton = {
            if (estado is Carga.Error) TextButton({ intento++ }) { Text("Reintentar") }
            else TextButton(onCerrar) { Text("Cerrar") }
        },
        dismissButton = { if (estado is Carga.Error) TextButton(onCerrar) { Text("Cerrar") } },
    )
}

@Composable
private fun Dato(etiqueta: String, valor: String?) {
    if (valor.isNullOrBlank()) return
    Column(Modifier.padding(vertical = 2.dp)) {
        Text(etiqueta, style = Texto.BodyM, color = TintaSuave)
        Text(valor, style = Texto.BodyL, color = TintaBase)
    }
}
