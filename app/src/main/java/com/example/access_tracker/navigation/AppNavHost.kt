package com.example.access_tracker.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.screens.comunes.AuthViewModel
import com.example.access_tracker.screens.comunes.perfilDeRol
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.access_tracker.components.BarraNavegacion
import com.example.access_tracker.screens.comunes.AvisoSinConexionScreen
import com.example.access_tracker.screens.comunes.CambioPasswordScreen
import com.example.access_tracker.screens.comunes.CambioPasswordViewModel
import com.example.access_tracker.screens.comunes.ExportarScreen
import com.example.access_tracker.screens.comunes.IndiceScreen
import com.example.access_tracker.screens.comunes.LoginScreen
import com.example.access_tracker.screens.comunes.PermisosScreen
import com.example.access_tracker.screens.comunes.SesionExpiradaScreen
import com.example.access_tracker.screens.direccion.DirBitacoraRoute
import com.example.access_tracker.screens.direccion.DirEstadisticasRoute
import com.example.access_tracker.screens.direccion.DirResumenRoute
import com.example.access_tracker.screens.empleada.AsistenciasRoute
import com.example.access_tracker.screens.empleada.AvisoDetalleScreen
import com.example.access_tracker.screens.empleada.ChecarDentroScreen
import com.example.access_tracker.screens.empleada.AvisoRoute
import com.example.access_tracker.screens.empleada.AvisosRoute
import com.example.access_tracker.screens.empleada.ChecarRoute
import com.example.access_tracker.screens.empleada.ChecarSinInternetScreen
import com.example.access_tracker.screens.empleada.HorarioRoute
import com.example.access_tracker.screens.responsable.AltaPersonalRoute
import com.example.access_tracker.screens.responsable.DarDeBajaRoute
import com.example.access_tracker.screens.responsable.PerfilPersonaRoute
import com.example.access_tracker.screens.responsable.RegistrarAusenciaRoute
import com.example.access_tracker.screens.responsable.RegistroTurnoRoute
import com.example.access_tracker.screens.responsable.ResAjustesSalasRoute
import com.example.access_tracker.screens.responsable.ResAsistenciaRoute
import com.example.access_tracker.screens.responsable.ResAvisosRoute
import com.example.access_tracker.screens.responsable.ResBitacoraRoute
import com.example.access_tracker.screens.responsable.ResEstadoLecturaRoute
import com.example.access_tracker.screens.responsable.ResHorarioPersonaRoute
import com.example.access_tracker.screens.responsable.ResHorariosRoute
import com.example.access_tracker.screens.responsable.ResInicioRoute
import com.example.access_tracker.screens.responsable.ResMenuScreen
import com.example.access_tracker.screens.responsable.ResNuevoAvisoRoute
import com.example.access_tracker.screens.responsable.ResPersonalRoute

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    // Si hay una sesion guardada y vigente, se entra directo; si no, login.
    val inicio = remember {
        val s = ServiceLocator.sesion.leer()
        val perfil = s?.takeIf { it.vigente() }?.let { perfilDeRol(it.role) }
        if (perfil != null) Pestanas.inicio(perfil) else ScreenNames.Login.route
    }

    // Cualquier llamada autenticada que reciba 401 (token vencido o revocado) cae aqui.
    LaunchedEffect(Unit) {
        ServiceLocator.api.sesionExpirada.collect {
            nav.navigate(ScreenNames.SesionExpirada.route) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        }
    }

    NavHost(navController = nav, startDestination = inicio) {
        // Comunes
        composable(ScreenNames.Login.route) {
            val vm: AuthViewModel = viewModel()
            val estado by vm.estado.collectAsState()
            LaunchedEffect(estado.perfil) {
                estado.perfil?.let {
                    vm.consumirPerfil()
                    // Sin cambio de contrasena obligatorio: de login directo a permisos.
                    nav.navigate(ScreenNames.Permisos.createRoute(it))
                }
            }
            LoginScreen(
                onIniciarSesion = { clave, password -> vm.iniciarSesion(clave, password) },
                onVerIndice = { nav.navigate(ScreenNames.Indice.route) },
                cargando = estado.cargando,
                error = estado.error,
            )
        }
        composable(
            route = ScreenNames.CambioPassword.route,
            arguments = listOf(navArgument("perfil") { type = NavType.StringType })
        ) {
            val vm: CambioPasswordViewModel = viewModel()
            val estado by vm.estado.collectAsState()
            // Cambio voluntario desde el menu de cuenta: al guardar o cancelar se vuelve a la pantalla anterior.
            LaunchedEffect(estado.listo) {
                if (estado.listo) {
                    vm.consumirListo()
                    nav.popBackStack()
                }
            }
            CambioPasswordScreen(
                onGuardar = { actual, nueva -> vm.guardar(actual, nueva) },
                onCancelar = { nav.popBackStack() },
                enviando = estado.enviando,
                error = estado.error,
            )
        }
        composable(
            route = ScreenNames.Permisos.route,
            arguments = listOf(navArgument("perfil") { type = NavType.StringType })
        ) { backStackEntry ->
            val perfil = Perfil.valueOf(backStackEntry.arguments?.getString("perfil") ?: "Empleada")
            val contexto = LocalContext.current
            // Pide ubicacion (precisa y aproximada) y, en Android 13+, notificaciones.
            // Se continua se acepte o no: sin ubicacion, las checadas quedan "fuera de zona".
            val pedirPermisos = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { entrarAPerfil(nav, perfil) }
            PermisosScreen(
                onContinuar = { entrarAPerfil(nav, perfil) },
                onPermitir = {
                    val permisos = buildList {
                        add(Manifest.permission.ACCESS_FINE_LOCATION)
                        add(Manifest.permission.ACCESS_COARSE_LOCATION)
                        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
                    }.filter {
                        ContextCompat.checkSelfPermission(contexto, it) != PackageManager.PERMISSION_GRANTED
                    }
                    if (permisos.isEmpty()) entrarAPerfil(nav, perfil)
                    else pedirPermisos.launch(permisos.toTypedArray())
                },
            )
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
            ChecarRoute(
                onAvisos = { nav.navigate(ScreenNames.EmpAvisos.route) },
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
            AsistenciasRoute(barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpAsistencias) })
        }
        composable(ScreenNames.EmpHorario.route) {
            HorarioRoute(barra = { Barra(nav, Perfil.Empleada, ScreenNames.EmpHorario) })
        }
        composable(ScreenNames.EmpAvisos.route) {
            AvisosRoute(
                onAtras = { nav.popBackStack() },
                onAbrir = { id -> nav.navigate(ScreenNames.EmpAviso.createRoute(id)) }
            )
        }
        composable(
            route = ScreenNames.EmpAviso.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            AvisoRoute(
                id = backStackEntry.arguments?.getLong("id") ?: 0L,
                onAtras = { nav.popBackStack() },
            )
        }
        composable(ScreenNames.EmpAvisoDetalle.route) {
            AvisoDetalleScreen(onAtras = { nav.popBackStack() }, onConfirmar = { nav.popBackStack() })
        }

        // Responsable
        composable(ScreenNames.ResInicio.route) {
            ResInicioRoute(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onMenu = { nav.navigate(ScreenNames.ResMenu.route) },
                onAtender = { irAPestana(nav, Perfil.Responsable, ScreenNames.ResAsistencia.route) },
                onVerAsistencia = { irAPestana(nav, Perfil.Responsable, ScreenNames.ResAsistencia.route) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResInicio) }
            )
        }
        composable(ScreenNames.ResAsistencia.route) {
            ResAsistenciaRoute(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onTurnoCompleto = { nav.navigate(ScreenNames.ResRegistroTurno.route) },
                onAusencia = { id -> nav.navigate(ScreenNames.ResAusencia.createRoute(id)) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResAsistencia) }
            )
        }
        composable(ScreenNames.ResHorarios.route) {
            ResHorariosRoute(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onPersona = { nav.navigate(ScreenNames.ResHorarioPersona.createRoute(it)) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResHorarios) }
            )
        }
        composable(
            route = ScreenNames.ResHorarioPersona.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            ResHorarioPersonaRoute(id = entry.arguments?.getLong("id") ?: 0L, onAtras = { nav.popBackStack() })
        }
        composable(ScreenNames.ResPersonal.route) {
            ResPersonalRoute(
                onAvisos = { nav.navigate(ScreenNames.ResAvisos.route) },
                onPersona = { nav.navigate(ScreenNames.ResPerfilPersona.createRoute(it)) },
                onAlta = { nav.navigate(ScreenNames.ResAltaPersonal.route) },
                barra = { Barra(nav, Perfil.Responsable, ScreenNames.ResPersonal) }
            )
        }
        composable(ScreenNames.ResRegistroTurno.route) {
            RegistroTurnoRoute(onCerrar = { nav.popBackStack() })
        }
        composable(
            route = ScreenNames.ResAusencia.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            RegistrarAusenciaRoute(userId = id.takeIf { it > 0 }, onCerrar = { nav.popBackStack() })
        }
        composable(ScreenNames.ResAltaPersonal.route) {
            AltaPersonalRoute(id = null, onCerrar = { nav.popBackStack() })
        }
        composable(
            route = ScreenNames.ResEditarPersonal.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            AltaPersonalRoute(id = entry.arguments?.getLong("id"), onCerrar = { nav.popBackStack() })
        }
        composable(
            route = ScreenNames.ResPerfilPersona.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            PerfilPersonaRoute(
                id = id,
                onAtras = { nav.popBackStack() },
                onEditar = { nav.navigate(ScreenNames.ResEditarPersonal.createRoute(id)) },
                onBaja = { nav.navigate(ScreenNames.ResDarBaja.createRoute(id)) },
                onHorario = { nav.navigate(ScreenNames.ResHorarioPersona.createRoute(id)) }
            )
        }
        composable(
            route = ScreenNames.ResDarBaja.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            DarDeBajaRoute(
                id = entry.arguments?.getLong("id") ?: 0L,
                onCerrar = { nav.popBackStack() },
                // Tras la baja o el traslado se vuelve a la lista de personal.
                onTerminado = { nav.popBackStack(ScreenNames.ResPersonal.route, inclusive = false) }
            )
        }
        composable(ScreenNames.ResAvisos.route) {
            ResAvisosRoute(
                onAtras = { nav.popBackStack() },
                onNuevo = { nav.navigate(ScreenNames.ResNuevoAviso.route) },
                onEstadoLectura = { nav.navigate(ScreenNames.ResEstadoLectura.createRoute(it)) },
                onAbrirRecibido = { nav.navigate(ScreenNames.EmpAviso.createRoute(it)) }
            )
        }
        composable(ScreenNames.ResNuevoAviso.route) {
            ResNuevoAvisoRoute(onCerrar = { nav.popBackStack() })
        }
        composable(
            route = ScreenNames.ResEstadoLectura.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            ResEstadoLecturaRoute(id = entry.arguments?.getLong("id") ?: 0L, onAtras = { nav.popBackStack() })
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
            ResAjustesSalasRoute(onAtras = { nav.popBackStack() })
        }
        composable(ScreenNames.ResBitacora.route) {
            ResBitacoraRoute(onAtras = { nav.popBackStack() })
        }

        // Dirección
        composable(ScreenNames.DirResumen.route) {
            DirResumenRoute(
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Direccion, ScreenNames.DirResumen) }
            )
        }
        composable(ScreenNames.DirEstadisticas.route) {
            DirEstadisticasRoute(
                onExportar = { nav.navigate(ScreenNames.Exportar.createRoute(Perfil.Direccion)) },
                onCambiarPassword = { nav.navigate(ScreenNames.CambioPassword.createRoute(Perfil.Direccion)) },
                onCerrarSesion = { cerrarSesion(nav) },
                barra = { Barra(nav, Perfil.Direccion, ScreenNames.DirEstadisticas) }
            )
        }
        composable(ScreenNames.DirBitacora.route) {
            DirBitacoraRoute(
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
    // Revoca el token en el servidor (y lo borra del telefono) sin esperar la respuesta.
    ServiceLocator.appScope.launch { ServiceLocator.api.logout() }
    nav.navigate(ScreenNames.Login.route) {
        popUpTo(nav.graph.id) { inclusive = true }
    }
}
