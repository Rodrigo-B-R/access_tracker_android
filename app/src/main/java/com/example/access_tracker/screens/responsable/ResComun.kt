package com.example.access_tracker.screens.responsable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.access_tracker.data.ApiError
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.Aviso
import com.example.access_tracker.model.Catalogos
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600
import com.example.access_tracker.ui.theme.Texto
import kotlinx.serialization.builtins.ListSerializer

/** Estado de una accion de escritura (guardar, enviar...). */
data class Accion(val enviando: Boolean = false, val error: String? = null, val listo: Boolean = false)

/** Mensaje en espanol para errores de escritura (los de la API vienen en ingles). */
fun ApiError.textoAccion(): String = when (this) {
    is ApiError.Servidor -> when (codigo) {
        400 -> "Los datos no son válidos. Revisa los campos."
        403 -> "No tienes permiso para hacer esto."
        404 -> "No se encontró lo que buscas."
        409 -> "Ya existe o choca con otro registro."
        else -> texto()
    }
    else -> texto()
}

/** Catalogos (puestos, turnos, motivos). Casi no cambian: se piden una vez por sesion de la app. */
object CatalogosCache {
    private var datos: Catalogos? = null

    suspend fun obtener(): ApiResult<Catalogos> {
        datos?.let { return ApiResult.Ok(it) }
        val r = ServiceLocator.api.get("/catalogs", Catalogos.serializer())
        if (r is ApiResult.Ok) datos = r.value
        return r
    }
}

private val PALETA = listOf(
    Petroleo100 to Petroleo700,
    Mostaza100 to TintaBase,
    Coral100 to TintaBase,
    Verde100 to Verde600,
)

/** (fondo, tinta) del avatar segun el id, para que cada persona conserve su color. */
internal fun paleta(id: Long): Pair<Color, Color> = PALETA[(id % PALETA.size).toInt()]

/** Cuenta de avisos sin leer para la campana. Si falla, queda en 0. */
@Composable
internal fun rememberAvisosSinLeer(): Int {
    var n by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        val r = ServiceLocator.api.get("/notices/inbox", ListSerializer(Aviso.serializer()))
        if (r is ApiResult.Ok) n = r.value.count { !it.read }
    }
    return n
}

/** Muestra una sola vez la contrasena temporal que genera la API. */
@Composable
internal fun DialogoPasswordTemporal(nombre: String, password: String, onListo: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = { TextButton(onClick = onListo) { Text("Listo") } },
        title = { Text("Contraseña temporal") },
        text = {
            Text(
                "$nombre deberá entrar con esta contraseña y cambiarla.\n\n$password\n\n" +
                    "Anótala ahora: no se vuelve a mostrar.",
                style = Texto.BodyL,
            )
        },
    )
}
