package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.access_tracker.screens.responsable.AltaPersonalScreen
import com.example.access_tracker.screens.responsable.DarDeBajaScreen
import com.example.access_tracker.screens.responsable.PerfilPersonaScreen
import com.example.access_tracker.screens.responsable.RegistrarAusenciaScreen
import com.example.access_tracker.screens.responsable.RegistroTurnoScreen
import com.example.access_tracker.screens.responsable.ResAjustesSalasScreen
import com.example.access_tracker.screens.responsable.ResAsistenciaScreen
import com.example.access_tracker.screens.responsable.ResAvisosScreen
import com.example.access_tracker.screens.responsable.ResBitacoraScreen
import com.example.access_tracker.screens.responsable.ResEstadoLecturaScreen
import com.example.access_tracker.screens.responsable.ResHorarioPersonaScreen
import com.example.access_tracker.screens.responsable.ResHorariosScreen
import com.example.access_tracker.screens.responsable.ResInicioScreen
import com.example.access_tracker.screens.responsable.ResMenuScreen
import com.example.access_tracker.screens.responsable.ResNuevoAvisoScreen
import com.example.access_tracker.screens.responsable.ResPersonalScreen

fun NavGraphBuilder.grafoResponsable(nav: NavController) {
    val atras = { nav.popBackStack() }
    val irAAvisos = { nav.navigate(ScreenNames.ResAvisos.route) }

    // Pestañas principales
    composable(ScreenNames.ResInicio.route) {
        ResInicioScreen(
            onAvisos = irAAvisos,
            onMenu = { nav.navigate(ScreenNames.ResMenu.route) },
            onAtender = { nav.irAPestana(Perfil.Responsable, ScreenNames.ResAsistencia.route) },
            onVerAsistencia = { nav.irAPestana(Perfil.Responsable, ScreenNames.ResAsistencia.route) },
        )
    }
    composable(ScreenNames.ResAsistencia.route) {
        ResAsistenciaScreen(
            onAvisos = irAAvisos,
            onTurnoCompleto = { nav.navigate(ScreenNames.ResRegistroTurno.route) },
            onAusencia = { persona ->
                nav.navigate(ScreenNames.ResAusencia.createRoute(persona ?: "Paola Reyes"))
            },
        )
    }
    composable(ScreenNames.ResHorarios.route) {
        ResHorariosScreen(onAvisos = irAAvisos, onPersona = { nav.navigate(ScreenNames.ResHorarioPersona.route) })
    }
    composable(ScreenNames.ResHorarioPersona.route) { ResHorarioPersonaScreen(onAtras = { atras() }, onGuardar = { atras() }) }
    composable(ScreenNames.ResPersonal.route) {
        ResPersonalScreen(
            onAvisos = irAAvisos,
            onPersona = { nav.navigate(ScreenNames.ResPerfilPersona.route) },
            onAlta = { nav.navigate(ScreenNames.ResAltaPersonal.route) },
        )
    }

    // Registro de asistencia
    composable(ScreenNames.ResRegistroTurno.route) { RegistroTurnoScreen(onCerrar = { atras() }, onRegistrar = { atras() }) }
    composable(
        route = ScreenNames.ResAusencia.route,
        arguments = listOf(navArgument("persona") { type = NavType.StringType })
    ) { backStackEntry ->
        RegistrarAusenciaScreen(
            onCerrar = { atras() },
            onGuardar = { atras() },
            persona = backStackEntry.arguments?.getString("persona") ?: "Paola Reyes",
        )
    }

    // Personal
    composable(ScreenNames.ResAltaPersonal.route) { AltaPersonalScreen(onCerrar = { atras() }, onGuardar = { atras() }) }
    composable(ScreenNames.ResPerfilPersona.route) {
        PerfilPersonaScreen(
            onAtras = { atras() },
            onEditar = { nav.navigate(ScreenNames.ResAltaPersonal.route) },
            onBaja = { nav.navigate(ScreenNames.ResDarBaja.route) },
            onHorario = { nav.navigate(ScreenNames.ResHorarioPersona.route) },
        )
    }
    composable(ScreenNames.ResDarBaja.route) { DarDeBajaScreen(onCerrar = { atras() }, onConfirmar = { atras() }) }

    // Avisos internos
    composable(ScreenNames.ResAvisos.route) {
        ResAvisosScreen(
            onAtras = { atras() },
            onNuevo = { nav.navigate(ScreenNames.ResNuevoAviso.route) },
            onEstadoLectura = { nav.navigate(ScreenNames.ResEstadoLectura.route) },
        )
    }
    composable(ScreenNames.ResNuevoAviso.route) { ResNuevoAvisoScreen(onCerrar = { atras() }, onEnviar = { atras() }) }
    composable(ScreenNames.ResEstadoLectura.route) { ResEstadoLecturaScreen(onAtras = { atras() }, onRecordatorio = { atras() }) }

    // Menú, salas y mínimos, bitácora
    composable(ScreenNames.ResMenu.route) {
        ResMenuScreen(
            onCerrar = { atras() },
            onBitacora = { nav.navigate(ScreenNames.ResBitacora.route) },
            onSalas = { nav.navigate(ScreenNames.ResAjustesSalas.route) },
            onExportar = { nav.navigate(ScreenNames.Exportar.createRoute(Perfil.Responsable)) },
            onCuenta = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Responsable)) },
            onCerrarSesion = { nav.cerrarSesion() },
        )
    }
    composable(ScreenNames.ResAjustesSalas.route) { ResAjustesSalasScreen(onAtras = { atras() }, onGuardar = { atras() }) }
    composable(ScreenNames.ResBitacora.route) { ResBitacoraScreen(onAtras = { atras() }) }
}
