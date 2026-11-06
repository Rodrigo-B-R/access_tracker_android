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

    fun inicio(perfil: Perfil) = de(perfil).first().ruta
}
