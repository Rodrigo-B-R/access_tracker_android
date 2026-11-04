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
    val irAAvisos = { nav.navigate(Rutas.EmpAvisos) }
    val cambiarPassword = { nav.navigate(rutaCambioPassword(Perfil.Empleada)) }
    val salir = { nav.cerrarSesion() }

    composable(Rutas.EmpChecar) {
        ChecarFueraScreen(
            onAvisos = irAAvisos,
            onRegistrarEntrada = { nav.navigate(Rutas.EmpChecarDentro) },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(Rutas.EmpChecarDentro) {
        ChecarDentroScreen(
            onAvisos = irAAvisos,
            onRegistrarSalida = { nav.popBackStack() },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(Rutas.EmpChecarSinInternet) {
        ChecarSinInternetScreen(
            onAvisos = irAAvisos,
            onRegistrarSalida = { nav.popBackStack() },
            onCambiarPassword = { cambiarPassword() },
            onCerrarSesion = { salir() },
        )
    }
    composable(Rutas.EmpAsistencias) {
        AsistenciasScreen()
    }
    composable(Rutas.EmpHorario) { HorarioScreen() }
    composable(Rutas.EmpAvisos) {
        AvisosBandejaScreen(onAtras = { nav.popBackStack() }, onAbrirUrgente = { nav.navigate(Rutas.EmpAvisoDetalle) })
    }
    composable(Rutas.EmpAvisoDetalle) {
        AvisoDetalleScreen(onAtras = { nav.popBackStack() }, onConfirmar = { nav.popBackStack() })
    }
}
