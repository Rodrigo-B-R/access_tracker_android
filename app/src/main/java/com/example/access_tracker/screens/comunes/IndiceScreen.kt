package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.navigation.IndicePantallas
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

@Composable
fun IndiceScreen(onAtras: () -> Unit, onAbrir: (String) -> Unit) {
    PantallaBase(separacion = 12.dp) {
        EncabezadoPantalla("Pantallas del diseño", onAtras = onAtras, grande = false)
        IndicePantallas.secciones.forEach { seccion ->
            Eyebrow("${seccion.nombre} · ${seccion.entradas.size}", Modifier.padding(top = 8.dp))
            Tarjeta(relleno = 4.dp) {
                seccion.entradas.forEach { entrada ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onAbrir(entrada.ruta) }.padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(entrada.titulo, style = Texto.LabelL, color = TintaBase, modifier = Modifier.weight(1f))
                        Icono(Ico.Chevron, tint = TintaTenue, tamano = 20.dp)
                    }
                }
            }
        }
    }
}
