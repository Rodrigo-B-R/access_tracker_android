package com.example.access_tracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.access_tracker.screens.comunes.AvisoSinConexionScreen
import com.example.access_tracker.screens.comunes.CambioPasswordScreen
import com.example.access_tracker.screens.comunes.IndiceScreen
import com.example.access_tracker.screens.comunes.LoginScreen
import com.example.access_tracker.screens.comunes.PermisosScreen
import com.example.access_tracker.screens.comunes.SesionExpiradaScreen

private const val ArgPerfil = "perfil"
private val ArgumentosPerfil = listOf(
    navArgument(ArgPerfil) { type = NavType.StringType; defaultValue = Perfil.Empleada.name },
)

private fun perfilDe(nombre: String?) = Perfil.entries.firstOrNull { it.name == nombre } ?: Perfil.Empleada

fun NavGraphBuilder.grafoComunes(nav: NavController) {
    composable(Rutas.Login) {
        LoginScreen(
            // La cuenta decide el perfil: aquí se simula por la clave escrita.
            onIniciarSesion = { clave ->
                val c = clave.lowercase()
                val perfil = when {
                    "sofia" in c || "asist" in c || "resp" in c -> Perfil.Responsable
                    "dir" in c -> Perfil.Direccion
                    else -> Perfil.Empleada
                }
                nav.navigate("${Rutas.CambioPassword}?$ArgPerfil=${perfil.name}")
            },
            onVerIndice = { nav.navigate(Rutas.Indice) },
        )
    }
    composable("${Rutas.CambioPassword}?$ArgPerfil={$ArgPerfil}", arguments = ArgumentosPerfil) { entrada ->
        val perfil = perfilDe(entrada.arguments?.getString(ArgPerfil))
        CambioPasswordScreen(onGuardar = { nav.navigate("${Rutas.Permisos}?$ArgPerfil=${perfil.name}") })
    }
    composable("${Rutas.Permisos}?$ArgPerfil={$ArgPerfil}", arguments = ArgumentosPerfil) { entrada ->
        val perfil = perfilDe(entrada.arguments?.getString(ArgPerfil))
        PermisosScreen(onContinuar = { nav.entrarAPerfil(perfil) })
    }
    composable(Rutas.SesionExpirada) { SesionExpiradaScreen(onIniciarSesion = { nav.cerrarSesion() }) }
    composable(Rutas.AvisoSinConexion) { AvisoSinConexionScreen() }
    composable(Rutas.Indice) {
        IndiceScreen(onAtras = { nav.popBackStack() }, onAbrir = { nav.navigate(it) })
    }
}
