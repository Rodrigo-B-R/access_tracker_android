package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoCrema

/** Espacio que deja libre la barra de navegación flotante al final del contenido. */
val EspacioBarra: Dp = 130.dp

/**
 * Contenedor estándar: fondo crema, contenido con scroll a 24 dp de margen
 * y, opcionalmente, una cabecera a todo el ancho (bajo la barra de estado),
 * un pie fijo (botones de acción) y un overlay (botón flotante, etc.).
 */
@Composable
fun PantallaBase(
    modifier: Modifier = Modifier,
    fondo: Color = FondoCrema,
    conBarra: Boolean = false,
    separacion: Dp = 18.dp,
    cabecera: (@Composable ColumnScope.() -> Unit)? = null,
    pie: (@Composable ColumnScope.() -> Unit)? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier.fillMaxSize().background(fondo)) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                cabecera?.invoke(this)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (cabecera == null) Modifier.statusBarsPadding() else Modifier)
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = if (conBarra) EspacioBarra else 24.dp),
                    verticalArrangement = Arrangement.spacedBy(separacion),
                    content = contenido,
                )
            }
            if (pie != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(fondo)
                        .navigationBarsPadding()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = pie,
                )
            }
        }
        overlay?.invoke(this)
    }
}
