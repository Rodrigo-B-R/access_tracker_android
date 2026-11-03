package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.access_tracker.screens.direccion.DirAltaEstanciaScreen
import com.example.access_tracker.screens.direccion.DirBajasTrasladosScreen
import com.example.access_tracker.screens.direccion.DirBitacoraScreen
import com.example.access_tracker.screens.direccion.DirDetalleEstanciaScreen
import com.example.access_tracker.screens.direccion.DirEstadisticasScreen
import com.example.access_tracker.screens.direccion.DirEstanciasScreen
import com.example.access_tracker.screens.direccion.DirExportarScreen
import com.example.access_tracker.screens.direccion.DirResumenScreen

fun NavGraphBuilder.grafoDireccion(nav: NavController) {
    val atras = { nav.popBackStack() }
    val irADetalle = { nav.navigate(Rutas.DirDetalleEstancia) }

    composable(Rutas.DirResumen) { DirResumenScreen(onEstancia = { irADetalle() }) }
    composable(Rutas.DirEstancias) {
        DirEstanciasScreen(onEstancia = { irADetalle() }, onAlta = { nav.navigate(Rutas.DirAltaEstancia) })
    }
    composable(Rutas.DirEstadisticas) {
        DirEstadisticasScreen(onBajasTraslados = { nav.navigate(Rutas.DirBajasTraslados) })
    }
    composable(Rutas.DirBitacora) { DirBitacoraScreen() }

    composable(Rutas.DirDetalleEstancia) {
        DirDetalleEstanciaScreen(
            onAtras = { atras() },
            onEstadisticas = { nav.irAPestana(Perfil.Direccion, Rutas.DirEstadisticas) },
            onBitacora = { nav.irAPestana(Perfil.Direccion, Rutas.DirBitacora) },
        )
    }
    composable(Rutas.DirAltaEstancia) { DirAltaEstanciaScreen(onCerrar = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.DirBajasTraslados) { DirBajasTrasladosScreen(onAtras = { atras() }) }
    composable(Rutas.DirExportar) { DirExportarScreen(onAtras = { atras() }, onDescargar = { atras() }) }
}
