package com.example.access_tracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.access_tracker.components.BarraNavegacion
import com.example.access_tracker.screens.comunes.AvisoSinConexionScreen
import com.example.access_tracker.screens.comunes.CambioPasswordScreen
import com.example.access_tracker.screens.comunes.ExportarScreen
import com.example.access_tracker.screens.comunes.IndiceScreen
import com.example.access_tracker.screens.comunes.LoginScreen
import com.example.access_tracker.screens.comunes.PermisosScreen
import com.example.access_tracker.screens.comunes.SesionExpiradaScreen
import com.example.access_tracker.screens.direccion.DirBitacoraScreen
import com.example.access_tracker.screens.direccion.DirEstadisticasScreen
import com.example.access_tracker.screens.direccion.DirResumenScreen
import com.example.access_tracker.screens.empleada.AsistenciasScreen
import com.example.access_tracker.screens.empleada.AvisoDetalleScreen
import com.example.access_tracker.screens.empleada.AvisosBandejaScreen
import com.example.access_tracker.screens.empleada.ChecarDentroScreen
import com.example.access_tracker.screens.empleada.ChecarFueraScreen
import com.example.access_tracker.screens.empleada.ChecarSinInternetScreen
import com.example.access_tracker.screens.empleada.HorarioScreen
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

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = ScreenNames.Login.route) {
        // Comunes
        composable(ScreenNames.Login.route) {
            LoginScreen(
                onIniciarSesion = { clave ->
                    val c = clave.lowercase()
                    val perfil = when {
                        "sofia" in c || "asist" in c || "resp" in c -> Perfil.Responsable
                        "dir" in c -> Perfil.Direccion
                        else -> Perfil.Empleada
                    }
                    nav.navigate(ScreenNames.CambioPassword.createRoute(perfil))
                },
                onVerIndice = { nav.navigate(ScreenNames.Indice.route) }
            )
        }
        composable(
            route = ScreenNames.CambioPassword.route,
            arguments = listOf(navArgument("perfil") { type = NavType.StringType })
        ) { backStackEntry ->
            val perfil = Perfil.valueOf(backStackEntry.arguments?.getString("perfil") ?: "Empleada")
            CambioPasswordScreen(onGuardar = { nav.navigate(ScreenNames.Permisos.createRoute(perfil)) })
        }
        composable(
            route = ScreenNames.Permisos.route,
            arguments = listOf(navArgument("perfil") { type = NavType.StringType })
        ) { backStackEntry ->
            val perfil = Perfil.valueOf(backStackEntry.arguments?.getString("perfil") ?: "Empleada")
            PermisosScreen(onContinuar = { entrarAPerfil(nav, perfil) })
        }
        composable(
            route = ScreenNames.Exportar.route,
            arguments = listOf(navArgument("perfil") { type = NavType.StringType })
        ) { backStackEntry ->
            val perfil = backStackEntry.arguments?.getString("perfil")
            ExportarScreen(
                onAtras = { nav.popBackStack() },
                onDescargar = { nav.popBackStack() },
                direccion = perfil == "Direccion"
            )
        }
        composable(ScreenNames.SesionExpirada.route) {
            SesionExpiradaScreen(onIniciarSesion = { cerrarSesion(nav) })
        }
        composable(ScreenNames.AvisoSinConexion.route) {
            AvisoSinConexionScreen()
        }
        composable(ScreenNames.Indice.route) {
            IndiceScreen(onAtras = { nav.popBackStack() }, onAbrir = { nav.navigate(it) })
        }

        // Empleada
        composable(ScreenNames.EmpChecar.route) {
            ChecarFueraScreen(
                onAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) },
                onRegistrarEntrada = { nav.navigate(ScreenNames.EmpChecarDentro.route) },
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Empleada)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpChecar) }
            )
        }
        composable(ScreenNames.EmpChecarDentro.route) {
            ChecarDentroScreen(
                onAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) },
                onRegistrarSalida = { nav.popBackStack() },
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Empleada)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpChecar) }
            )
        }
        composable(ScreenNames.EmpChecarSinInternet.route) {
            ChecarSinInternetScreen(
                onAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) },
                onRegistrarSalida = { nav.popBackStack() },
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Empleada)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpChecar) }
            )
        }
        composable(ScreenNames.EmpAsistencias.route) {
            AsistenciasScreen(barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpAsistencias) })
        }
        composable(ScreenNames.EmpHorario.route) {
            HorarioScreen(barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpHorario) })
        }
        composable(ScreenNames.EmpAvisos.route) {
            AvisosBandejaScreen(
                onAtras = { nav.popBackStack() },
                onAbrirUrgente = { nav.navigate(ScreenNames.EmpAvisoDetalle.route) }
            )
        }
        composable(ScreenNames.EmpAvisoDetalle.route) {
            AvisoDetalleScreen(onAtras = { nav.popBackStack() }, onConfirmar = { nav.popBackStack() })
        }

        // Responsable
        composable(ScreenNames.ResInicio.route) {
            ResInicioScreen(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onMenu = { nav.navigate(ScreenNames.ResMenu.route) },
                onAtender = { irAPestana(nav, Perfil.Responsable, ScreenNames.ResAsistencia.route) },
                onVerAsistencia = { irAPestana(nav, Perfil.Responsable, ScreenNames.ResAsistencia.route) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResInicio) }
            )
        }
        composable(ScreenNames.ResAsistencia.route) {
            ResAsistenciaScreen(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onTurnoCompleto = { nav.navigate(ScreenNames.ResRegistroTurno.route) },
                onAusencia = { persona ->
                    nav.navigate(ScreenNames.ResAusencia.createRoute(persona ?: "Paola Reyes"))
                },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResAsistencia) }
            )
        }
        composable(ScreenNames.ResHorarios.route) {
            ResHorariosScreen(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onPersona = { nav.navigate(ScreenNames.ResHorarioPersona.route) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResHorarios) }
            )
        }
        composable(ScreenNames.ResHorarioPersona.route) {
            ResHorarioPersonaScreen(onAtras = { nav.popBackStack() }, onGuardar = { nav.popBackStack() })
        }
        composable(ScreenNames.ResPersonal.route) {
            ResPersonalScreen(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onPersona = { nav.navigate(ScreenNames.ResPerfilPersona.route) },
                onAlta = { nav.navigate(ScreenNames.ResAltaPersonal.route) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResPersonal) }
            )
        }
        composable(ScreenNames.ResRegistroTurno.route) {
            RegistroTurnoScreen(onCerrar = { nav.popBackStack() }, onRegistrar = { nav.popBackStack() })
        }
        composable(
            route = ScreenNames.ResAusencia.route,
            arguments = listOf(navArgument("persona") { type = NavType.StringType })
        ) { backStackEntry ->
            RegistrarAusenciaScreen(
                onCerrar = { nav.popBackStack() },
                onGuardar = { nav.popBackStack() },
                persona = backStackEntry.arguments?.getString("persona") ?: "Paola Reyes"
            )
        }
        composable(ScreenNames.ResAltaPersonal.route) {
            AltaPersonalScreen(onCerrar = { nav.popBackStack() }, onGuardar = { nav.popBackStack() })
        }
        composable(ScreenNames.ResPerfilPersona.route) {
            PerfilPersonaScreen(
                onAtras = { nav.popBackStack() },
                onEditar = { nav.navigate(ScreenNames.ResAltaPersonal.route) },
                onBaja = { nav.navigate(ScreenNames.ResDarBaja.route) },
                onHorario = { nav.navigate(ScreenNames.ResHorarioPersona.route) }
            )
        }
        composable(ScreenNames.ResDarBaja.route) {
            DarDeBajaScreen(onCerrar = { nav.popBackStack() }, onConfirmar = { nav.popBackStack() })
        }
        composable(ScreenNames.ResAvisos.route) {
            ResAvisosScreen(
                onAtras = { nav.popBackStack() },
                onNuevo = { nav.navigate(ScreenNames.ResNuevoAviso.route) },
                onEstadoLectura = { nav.navigate(ScreenNames.ResEstadoLectura.route) }
            )
        }
        composable(ScreenNames.ResNuevoAviso.route) {
            ResNuevoAvisoScreen(onCerrar = { nav.popBackStack() }, onEnviar = { nav.popBackStack() })
        }
        composable(ScreenNames.ResEstadoLectura.route) {
            ResEstadoLecturaScreen(onAtras = { nav.popBackStack() }, onRecordatorio = { nav.popBackStack() })
        }
        composable(ScreenNames.ResMenu.route) {
            ResMenuScreen(
                onCerrar = { nav.popBackStack() },
                onBitacora = { nav.navigate(ScreenNames.ResBitacora.route) },
                onSalas = { nav.navigate(ScreenNames.ResAjustesSalas.route) },
                onExportar = { nav.navigate(ScreenNames.Exportar.createRoute(Perfil.Responsable)) },
                onCuenta = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Responsable)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResInicio) }
            )
        }
        composable(ScreenNames.ResAjustesSalas.route) {
            ResAjustesSalasScreen(onAtras = { nav.popBackStack() }, onGuardar = { nav.popBackStack() })
        }
        composable(ScreenNames.ResBitacora.route) {
            ResBitacoraScreen(onAtras = { nav.popBackStack() })
        }

        // Dirección
        composable(ScreenNames.DirResumen.route) {
            DirResumenScreen(
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Direccion, ScreenNames.DirResumen) }
            )
        }
        composable(ScreenNames.DirEstadisticas.route) {
            DirEstadisticasScreen(
                onExportar = { nav.navigate(ScreenNames.Exportar.createRoute(Perfil.Direccion)) },
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Direccion, ScreenNames.DirEstadisticas) }
            )
        }
        composable(ScreenNames.DirBitacora.route) {
            DirBitacoraScreen(
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Direccion, ScreenNames.DirBitacora) }
            )
        }
    }
}

@Composable
fun Barra(nav: NavController, perfil: Perfil, activa: ScreenNames) {
    BarraNavegacion(
        pestanas = Pestanas.de(perfil),
        activa = activa.route,
        onSeleccion = { irAPestana(nav, perfil, it.ruta) }
    )
}

fun irAPestana(nav: NavController, perfil: Perfil, ruta: String) {
    nav.navigate(ruta) {
        popUpTo(Pestanas.inicio(perfil))
        launchSingleTop = true
    }
}

fun entrarAPerfil(nav: NavController, perfil: Perfil) {
    nav.navigate(Pestanas.inicio(perfil)) {
        popUpTo(nav.graph.id) { inclusive = true }
    }
}

fun cerrarSesion(nav: NavController) {
    nav.navigate(ScreenNames.Login.route) {
        popUpTo(nav.graph.id) { inclusive = true }
    }
}
