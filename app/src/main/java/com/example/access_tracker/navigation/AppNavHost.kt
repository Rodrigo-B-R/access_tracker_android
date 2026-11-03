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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.access_tracker.components.BarraNavegacion
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo700

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val perfil = Pestanas.perfilDe(ruta)
    // «Checar · dentro» es la única pestaña con fondo petróleo: el desvanecido de la barra lo acompaña.
    val fondoPantalla = if (ruta == Rutas.EmpChecarDentro) Petroleo700 else FondoCrema

    Box(Modifier.fillMaxSize().background(FondoCrema)) {
        NavHost(navController = nav, startDestination = Rutas.Login) {
            grafoComunes(nav)
            grafoEmpleada(nav)
            grafoResponsable(nav)
            grafoDireccion(nav)
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
                    .alpha(if (ruta == Rutas.ResMenu) 0.55f else 1f),
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
    navigate(Rutas.Login) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
        launchSingleTop = true
    }
}
