package com.example.access_tracker.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo700

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val perfil = Pestanas.perfilDe(ruta)
    // «Checar · dentro» es la única pestaña con fondo petróleo: el desvanecido de la barra lo acompaña.
    val fondoPantalla = if (ruta == ScreenNames.EmpChecarDentro.route) Petroleo700 else FondoCrema

    Box(Modifier.fillMaxSize().background(FondoCrema)) {
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
                PermisosScreen(onContinuar = { nav.entrarAPerfil(perfil) })
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
                SesionExpiradaScreen(onIniciarSesion = { nav.cerrarSesion() })
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
                    onCerrarSesion = { nav.cerrarSesion() }
                )
            }
            composable(ScreenNames.EmpChecarDentro.route) {
                ChecarDentroScreen(
                    onAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) },
                    onRegistrarSalida = { nav.popBackStack() },
                    onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Empleada)) },
                    onCerrarSesion = { nav.cerrarSesion() }
                )
            }
            composable(ScreenNames.EmpChecarSinInternet.route) {
                ChecarSinInternetScreen(
                    onAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) },
                    onRegistrarSalida = { nav.popBackStack() },
                    onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Empleada)) },
                    onCerrarSesion = { nav.cerrarSesion() }
                )
            }
            composable(ScreenNames.EmpAsistencias.route) {
                AsistenciasScreen()
            }
            composable(ScreenNames.EmpHorario.route) {
                HorarioScreen()
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
                    onAtender = { nav.irAPestana(Perfil.Responsable, ScreenNames.ResAsistencia.route) },
                    onVerAsistencia = { nav.irAPestana(Perfil.Responsable, ScreenNames.ResAsistencia.route) }
                )
            }
            composable(ScreenNames.ResAsistencia.route) {
                ResAsistenciaScreen(
                    onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                    onTurnoCompleto = { nav.navigate(ScreenNames.ResRegistroTurno.route) },
                    onAusencia = { persona ->
                        nav.navigate(ScreenNames.ResAusencia.createRoute(persona ?: "Paola Reyes"))
                    }
                )
            }
            composable(ScreenNames.ResHorarios.route) {
                ResHorariosScreen(
                    onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                    onPersona = { nav.navigate(ScreenNames.ResHorarioPersona.route) }
                )
            }
            composable(ScreenNames.ResHorarioPersona.route) {
                ResHorarioPersonaScreen(onAtras = { nav.popBackStack() }, onGuardar = { nav.popBackStack() })
            }
            composable(ScreenNames.ResPersonal.route) {
                ResPersonalScreen(
                    onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                    onPersona = { nav.navigate(ScreenNames.ResPerfilPersona.route) },
                    onAlta = { nav.navigate(ScreenNames.ResAltaPersonal.route) }
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
                    onCerrarSesion = { nav.cerrarSesion() }
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
                    onCerrarSesion = { nav.cerrarSesion() }
                )
            }
            composable(ScreenNames.DirEstadisticas.route) {
                DirEstadisticasScreen(
                    onExportar = { nav.navigate(ScreenNames.Exportar.createRoute(Perfil.Direccion)) },
                    onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                    onCerrarSesion = { nav.cerrarSesion() }
                )
            }
            composable(ScreenNames.DirBitacora.route) {
                DirBitacoraScreen(
                    onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                    onCerrarSesion = { nav.cerrarSesion() }
                )
            }
        }

        // La barra flotante solo aparece en las pestañas principales de cada perfil.
        if (perfil != null) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(Brush.verticalGradient(listOf(fondoPantalla.copy(alpha = 0f), fondoPantalla.copy(alpha = 0.95f), fondoPantalla))),
            )
            BarraNavegacion(
                pestanas = Pestanas.de(perfil),
                activa = Pestanas.activaDe(ruta),
                onSeleccion = { nav.irAPestana(perfil, it.ruta) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    // Bajo el velo del menú ⋮ la barra se ve atenuada.
                    .alpha(if (ruta == ScreenNames.ResMenu.route) 0.55f else 1f),
            )
        }
    }
}

/** Cambia de pestaña conservando el estado de cada una. */
fun NavController.irAPestana(perfil: Perfil, ruta: String) {
    navigate(ruta) {
        popUpTo(Pestanas.inicio(perfil)) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Entra a un perfil y limpia el login de la pila. */
fun NavController.entrarAPerfil(perfil: Perfil) {
    navigate(Pestanas.inicio(perfil)) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Cierra la sesión y vuelve al login. */
fun NavController.cerrarSesion() {
    navigate(ScreenNames.Login.route) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
        launchSingleTop = true
    }
}
