package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.access_tracker.screens.direccion.DirBitacoraScreen
import com.example.access_tracker.screens.direccion.DirEstadisticasScreen
import com.example.access_tracker.screens.direccion.DirResumenScreen

fun NavGraphBuilder.grafoDireccion(nav: NavController) {
    val cambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) }
    val salir = { nav.cerrarSesion() }

    composable(ScreenNames.DirResumen.route) {
        DirResumenScreen(onCambiarPassword = { cambiarPassword() }, onCerrarSesion = { salir() })
    }
    composable(ScreenNames.DirEstadisticas.route) {
        DirEstadisticasScreen(
            onExportar = { nav.navigate(ScreenNames.Exportar.createRoute(Perfil.Direccion)) },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(ScreenNames.DirBitacora.route) {
        DirBitacoraScreen(onCambiarPassword = { cambiarPassword() }, onCerrarSesion = { salir() })
    }
}
