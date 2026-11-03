package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.access_tracker.screens.responsable.AlertaInsuficienteScreen
import com.example.access_tracker.screens.responsable.AltaPersonalScreen
import com.example.access_tracker.screens.responsable.CorregirRegistroScreen
import com.example.access_tracker.screens.responsable.DarDeBajaScreen
import com.example.access_tracker.screens.responsable.PerfilPersonaScreen
import com.example.access_tracker.screens.responsable.RegistrarAusenciaScreen
import com.example.access_tracker.screens.responsable.RegistroTurnoScreen
import com.example.access_tracker.screens.responsable.ResAjustesPermisosScreen
import com.example.access_tracker.screens.responsable.ResAjustesScreen
import com.example.access_tracker.screens.responsable.ResAjustesSalasScreen
import com.example.access_tracker.screens.responsable.ResAsistenciaScreen
import com.example.access_tracker.screens.responsable.ResAvisosScreen
import com.example.access_tracker.screens.responsable.ResBitacoraScreen
import com.example.access_tracker.screens.responsable.ResEstadoLecturaScreen
import com.example.access_tracker.screens.responsable.ResExportarScreen
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
            onAtender = { nav.navigate(Rutas.ResAlertaInsuficiente) },
            onVerAsistencia = { nav.irAPestana(Perfil.Responsable, Rutas.ResAsistencia) },
        )
    }
    composable(Rutas.ResAsistencia) {
        ResAsistenciaScreen(
            onAvisos = irAAvisos,
            onTurnoCompleto = { nav.navigate(Rutas.ResRegistroTurno) },
            onAusencia = { nav.navigate(Rutas.ResAusencia) },
            onCorregir = { nav.navigate(Rutas.ResCorregir) },
        )
    }
    composable(Rutas.ResHorarios) { ResHorariosScreen(onAvisos = irAAvisos) }
    composable(Rutas.ResPersonal) {
        ResPersonalScreen(
            onAvisos = irAAvisos,
            onPersona = { nav.navigate(Rutas.ResPerfilPersona) },
            onAlta = { nav.navigate(Rutas.ResAltaPersonal) },
        )
    }

    // Registro de asistencia
    composable(Rutas.ResRegistroTurno) { RegistroTurnoScreen(onCerrar = { atras() }, onRegistrar = { atras() }) }
    composable(Rutas.ResAusencia) { RegistrarAusenciaScreen(onCerrar = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.ResAlertaInsuficiente) { AlertaInsuficienteScreen(onVerCobertura = { atras() }, onCerrar = { atras() }) }
    composable(Rutas.ResCorregir) { CorregirRegistroScreen(onAtras = { atras() }, onGuardar = { atras() }) }

    // Personal
    composable(Rutas.ResAltaPersonal) { AltaPersonalScreen(onCerrar = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.ResPerfilPersona) {
        PerfilPersonaScreen(
            onAtras = { atras() },
            onEditar = { nav.navigate(Rutas.ResAltaPersonal) },
            onBaja = { nav.navigate(Rutas.ResDarBaja) },
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

    // Menú, ajustes, bitácora y exportar
    composable(Rutas.ResMenu) {
        ResMenuScreen(
            onCerrar = { atras() },
            onBitacora = { nav.navigate(Rutas.ResBitacora) },
            onAjustes = { nav.navigate(Rutas.ResAjustes) },
            onExportar = { nav.navigate(Rutas.ResExportar) },
        )
    }
    composable(Rutas.ResAjustes) {
        ResAjustesScreen(
            onAtras = { atras() },
            onSalas = { nav.navigate(Rutas.ResAjustesSalas) },
            onPermisos = { nav.navigate(Rutas.ResAjustesPermisos) },
            onCerrarSesion = { nav.cerrarSesion() },
        )
    }
    composable(Rutas.ResAjustesSalas) { ResAjustesSalasScreen(onAtras = { atras() }, onGuardar = { atras() }) }
    composable(Rutas.ResAjustesPermisos) { ResAjustesPermisosScreen(onAtras = { atras() }) }
    composable(Rutas.ResBitacora) { ResBitacoraScreen(onAtras = { atras() }) }
    composable(Rutas.ResExportar) { ResExportarScreen(onAtras = { atras() }, onDescargar = { atras() }) }
}
