package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.access_tracker.screens.comunes.AvisoSinConexionScreen
import com.example.access_tracker.screens.comunes.CambioPasswordScreen
import com.example.access_tracker.screens.comunes.ExportarScreen
import com.example.access_tracker.screens.comunes.IndiceScreen
import com.example.access_tracker.screens.comunes.LoginScreen
import com.example.access_tracker.screens.comunes.PermisosScreen
import com.example.access_tracker.screens.comunes.SesionExpiradaScreen

fun NavGraphBuilder.grafoComunes(nav: NavController) {
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
            onVerIndice = { nav.navigate(ScreenNames.Indice.route) },
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
            direccion = perfil == "Direccion",
        )
    }
    composable(ScreenNames.SesionExpirada.route) { SesionExpiradaScreen(onIniciarSesion = { nav.cerrarSesion() }) }
    composable(ScreenNames.AvisoSinConexion.route) { AvisoSinConexionScreen() }
    composable(ScreenNames.Indice.route) {
        IndiceScreen(onAtras = { nav.popBackStack() }, onAbrir = { nav.navigate(it) })
    }
}
