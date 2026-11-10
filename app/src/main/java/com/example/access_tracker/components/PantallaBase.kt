package com.example.access_tracker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoCrema

@Composable
fun PantallaBase(
    fondo: Color = FondoCrema,
    separacion: Dp = 18.dp,
    cabecera: (@Composable () -> Unit)? = null,
    pie: (@Composable ColumnScope.() -> Unit)? = null,
    barra: (@Composable () -> Unit)? = null,
    botonFlotante: @Composable () -> Unit = {},
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        // Con edge-to-edge, adjustResize no basta: sin esto el teclado tapa los campos.
        modifier = Modifier.imePadding(),
        containerColor = fondo,
        bottomBar = {
            if (barra != null) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    barra()
                }
            } else if (pie != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    pie()
                }
            }
        },
        floatingActionButton = botonFlotante
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            if (cabecera != null) {
                cabecera()
            } else {
                Spacer(Modifier.statusBarsPadding())
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(separacion)
            ) {
                contenido()
            }
        }
    }
}
