package com.example.access_tracker.navigation

import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Pestana

enum class Perfil { Empleada, Responsable, Direccion }

object Pestanas {
    val Empleada = listOf(
        Pestana(ScreenNames.EmpChecar.route, "Checar", Ico.Fingerprint),
        Pestana(ScreenNames.EmpAsistencias.route, "Asistencias", Ico.Calcheck),
        Pestana(ScreenNames.EmpHorario.route, "Horario", Ico.Calendar),
    )
    val Responsable = listOf(
        Pestana(ScreenNames.ResInicio.route, "Inicio", Ico.Home),
        Pestana(ScreenNames.ResAsistencia.route, "Asistencia", Ico.Users),
        Pestana(ScreenNames.ResHorarios.route, "Horarios", Ico.Calendar),
        Pestana(ScreenNames.ResPersonal.route, "Personal", Ico.Idcard),
    )
    val Direccion = listOf(
        Pestana(ScreenNames.DirResumen.route, "Resumen", Ico.Home),
        Pestana(ScreenNames.DirEstadisticas.route, "Estadísticas", Ico.Chart),
        Pestana(ScreenNames.DirBitacora.route, "Bitácora", Ico.History),
    )

    fun de(perfil: Perfil) = when (perfil) {
        Perfil.Empleada -> Empleada
        Perfil.Responsable -> Responsable
        Perfil.Direccion -> Direccion
    }

    // Variantes de estado de una pestaña: se ven con la barra y la misma pestaña activa.
    private val variantes = mapOf(
        ScreenNames.EmpChecarDentro.route to ScreenNames.EmpChecar.route,
        ScreenNames.EmpChecarSinInternet.route to ScreenNames.EmpChecar.route,
        ScreenNames.ResMenu.route to ScreenNames.ResInicio.route,
    )

    /** Pestaña que se marca como activa para una ruta (la misma ruta, o la pestaña de la que es variante). */
    fun activaDe(ruta: String?): String? = variantes[ruta] ?: ruta

    /** Perfil dueño de la ruta si es una pestaña principal (la barra solo se muestra ahí). */
    fun perfilDe(ruta: String?): Perfil? = activaDe(ruta).let { activa -> Perfil.entries.firstOrNull { p -> de(p).any { it.ruta == activa } } }

    fun inicio(perfil: Perfil) = de(perfil).first().ruta
}
