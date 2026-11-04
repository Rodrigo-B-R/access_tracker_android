package com.example.access_tracker.navigation

import android.net.Uri
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
    val irAAvisos = { nav.navigate(Rutas.ResAvisos) }

    // Pestañas principales
    composable(Rutas.ResInicio) {
        ResInicioScreen(
            onAvisos = irAAvisos,
            onMenu = { nav.navigate(Rutas.ResMenu) },
            onAtender = { nav.irAPestana(Perfil.Responsable, Rutas.ResAsistencia) },
            onVerAsistencia = { nav.irAPestana(Perfil.Responsable, Rutas.ResAsistencia) },
        )
    }
    composable(Rutas.ResAsistencia) {
        ResAsistenciaScreen(
            onAvisos = irAAvisos,
            onTurnoCompleto = { nav.navigate(Rutas.ResRegistroTurno) },
            onAusencia = { persona ->
                nav.navigate(if (persona == null) Rutas.ResAusencia else "${Rutas.ResAusencia}?persona=${Uri.encode(persona)}")
            },
        )
    }
    composable(Rutas.ResHorarios) {
        ResHorariosScreen(onAvisos = irAAvisos, onPersona = { nav.navigate(Rutas.ResHorarioPersona) })
    }
    composable(Rutas.ResHorarioPersona) { ResHorarioPersonaScreen(onAtras = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.ResPersonal) {
        ResPersonalScreen(
            onAvisos = irAAvisos,
            onPersona = { nav.navigate(Rutas.ResPerfilPersona) },
            onAlta = { nav.navigate(Rutas.ResAltaPersonal) },
        )
    }

    // Registro de asistencia
    composable(Rutas.ResRegistroTurno) { RegistroTurnoScreen(onCerrar = { atras() }, onRegistrar = { atras() }) }
    composable(
        "${Rutas.ResAusencia}?persona={persona}",
        arguments = listOf(navArgument("persona") { type = NavType.StringType; defaultValue = "Paola Reyes" }),
    ) { entrada ->
        RegistrarAusenciaScreen(
            onCerrar = { atras() },
            // Vuelve a Asistencia sin restaurar el estado guardado de la pestaña, que incluiría este formulario.
            onGuardar = {
                if (!nav.popBackStack(Rutas.ResAsistencia, inclusive = false)) nav.irAPestana(Perfil.Responsable, Rutas.ResAsistencia)
            },
            persona = entrada.arguments?.getString("persona") ?: "Paola Reyes",
        )
    }

    // Personal
    composable(Rutas.ResAltaPersonal) { AltaPersonalScreen(onCerrar = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.ResPerfilPersona) {
        PerfilPersonaScreen(
            onAtras = { atras() },
            onEditar = { nav.navigate(Rutas.ResAltaPersonal) },
            onBaja = { nav.navigate(Rutas.ResDarBaja) },
            onHorario = { nav.navigate(Rutas.ResHorarioPersona) },
        )
    }
    composable(Rutas.ResDarBaja) { DarDeBajaScreen(onCerrar = { atras() }, onConfirmar = { atras() }) }

    // Avisos internos
    composable(Rutas.ResAvisos) {
        ResAvisosScreen(
            onAtras = { atras() },
            onNuevo = { nav.navigate(Rutas.ResNuevoAviso) },
            onEstadoLectura = { nav.navigate(Rutas.ResEstadoLectura) },
        )
    }
    composable(Rutas.ResNuevoAviso) { ResNuevoAvisoScreen(onCerrar = { atras() }, onEnviar = { atras() }) }
    composable(Rutas.ResEstadoLectura) { ResEstadoLecturaScreen(onAtras = { atras() }, onRecordatorio = { atras() }) }

    // Menú, salas y mínimos, bitácora
    composable(Rutas.ResMenu) {
        ResMenuScreen(
            onCerrar = { atras() },
            onBitacora = { nav.navigate(Rutas.ResBitacora) },
            onSalas = { nav.navigate(Rutas.ResAjustesSalas) },
            onExportar = { nav.navigate(rutaExportar(Perfil.Responsable)) },
            onCuenta = { nav.navigate(rutaCambioPassword(Perfil.Responsable)) },
            onCerrarSesion = { nav.cerrarSesion() },
        )
    }
    composable(Rutas.ResAjustesSalas) { ResAjustesSalasScreen(onAtras = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.ResBitacora) { ResBitacoraScreen(onAtras = { atras() }) }
}
