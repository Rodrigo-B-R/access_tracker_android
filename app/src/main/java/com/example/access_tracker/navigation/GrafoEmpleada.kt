package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.access_tracker.screens.empleada.AsistenciasScreen
import com.example.access_tracker.screens.empleada.AvisoDetalleScreen
import com.example.access_tracker.screens.empleada.AvisosBandejaScreen
import com.example.access_tracker.screens.empleada.ChecarDentroScreen
import com.example.access_tracker.screens.empleada.ChecarFueraScreen
import com.example.access_tracker.screens.empleada.ChecarSinInternetScreen
import com.example.access_tracker.screens.empleada.HorarioScreen

fun NavGraphBuilder.grafoEmpleada(nav: NavController) {
    val irAAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) }
    val cambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Empleada)) }
    val salir = { nav.cerrarSesion() }

    composable(ScreenNames.EmpChecar.route) {
        ChecarFueraScreen(
            onAvisos = irAAvisos,
            onRegistrarEntrada = { nav.navigate(ScreenNames.EmpChecarDentro.route) },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(ScreenNames.EmpChecarDentro.route) {
        ChecarDentroScreen(
            onAvisos = irAAvisos,
            onRegistrarSalida = { nav.popBackStack() },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(ScreenNames.EmpChecarSinInternet.route) {
        ChecarSinInternetScreen(
            onAvisos = irAAvisos,
            onRegistrarSalida = { nav.popBackStack() },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(ScreenNames.EmpAsistencias.route) {
        AsistenciasScreen()
    }
    composable(ScreenNames.EmpHorario.route) { HorarioScreen() }
    composable(ScreenNames.EmpAvisos.route) {
        AvisosBandejaScreen(onAtras = { nav.popBackStack() }, onAbrirUrgente = { nav.navigate(ScreenNames.EmpAvisoDetalle.route) })
    }
    composable(ScreenNames.EmpAvisoDetalle.route) {
        AvisoDetalleScreen(onAtras = { nav.popBackStack() }, onConfirmar = { nav.popBackStack() })
    }
}
