package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.access_tracker.screens.direccion.DirBitacoraScreen
import com.example.access_tracker.screens.direccion.DirEstadisticasScreen
import com.example.access_tracker.screens.direccion.DirResumenScreen

fun NavGraphBuilder.grafoDireccion(nav: NavController) {
    val cambiarPassword = { nav.navigate(rutaCambioPassword(Perfil.Direccion)) }
    val salir = { nav.cerrarSesion() }

    composable(Rutas.DirResumen) {
        DirResumenScreen(onCambiarPassword = { cambiarPassword() }, onCerrarSesion = { salir() })
    }
    composable(Rutas.DirEstadisticas) {
        DirEstadisticasScreen(
            onExportar = { nav.navigate(rutaExportar(Perfil.Direccion)) },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(Rutas.DirBitacora) {
        DirBitacoraScreen(onCambiarPassword = { cambiarPassword() }, onCerrarSesion = { salir() })
    }
}
