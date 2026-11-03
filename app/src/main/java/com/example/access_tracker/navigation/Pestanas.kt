package com.example.access_tracker.navigation

import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Pestana

enum class Perfil { Empleada, Responsable, Direccion }

object Pestanas {
    val Empleada = listOf(
        Pestana(Rutas.EmpChecar, "Checar", Ico.Fingerprint),
        Pestana(Rutas.EmpAsistencias, "Asistencias", Ico.Calcheck),
        Pestana(Rutas.EmpHorario, "Horario", Ico.Calendar),
    )
    val Responsable = listOf(
        Pestana(Rutas.ResInicio, "Inicio", Ico.Home),
        Pestana(Rutas.ResAsistencia, "Asistencia", Ico.Users),
        Pestana(Rutas.ResHorarios, "Horarios", Ico.Calendar),
        Pestana(Rutas.ResPersonal, "Personal", Ico.Idcard),
    )
    val Direccion = listOf(
        Pestana(Rutas.DirResumen, "Resumen", Ico.Home),
        Pestana(Rutas.DirEstancias, "Estancias", Ico.Building),
        Pestana(Rutas.DirEstadisticas, "Estadísticas", Ico.Chart),
        Pestana(Rutas.DirBitacora, "Bitácora", Ico.History),
    )

    fun de(perfil: Perfil) = when (perfil) {
        Perfil.Empleada -> Empleada
        Perfil.Responsable -> Responsable
        Perfil.Direccion -> Direccion
    }

    // Variantes de estado de una pestaña: se ven con la barra y la misma pestaña activa.
    private val variantes = mapOf(
        Rutas.EmpChecarDentro to Rutas.EmpChecar,
        Rutas.EmpChecarSinInternet to Rutas.EmpChecar,
        Rutas.ResMenu to Rutas.ResInicio,
    )

    /** Pestaña que se marca como activa para una ruta (la misma ruta, o la pestaña de la que es variante). */
    fun activaDe(ruta: String?): String? = variantes[ruta] ?: ruta

    /** Perfil dueño de la ruta si es una pestaña principal (la barra solo se muestra ahí). */
    fun perfilDe(ruta: String?): Perfil? = activaDe(ruta).let { activa -> Perfil.entries.firstOrNull { p -> de(p).any { it.ruta == activa } } }

    fun inicio(perfil: Perfil) = de(perfil).first().ruta
}
