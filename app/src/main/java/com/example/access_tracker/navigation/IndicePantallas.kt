package com.example.access_tracker.navigation

data class EntradaIndice(val titulo: String, val ruta: String)

data class SeccionIndice(val nombre: String, val entradas: List<EntradaIndice>)

object IndicePantallas {
    val secciones = listOf(
        SeccionIndice(
            "Comunes",
            listOf(
                EntradaIndice("Inicio de sesión", ScreenNames.Login.route),
                EntradaIndice("Cambio de contraseña inicial", ScreenNames.CambioPassword.createRoute(Perfil.Empleada)),
                EntradaIndice(
                    "Permisos de ubicación y notificaciones",
                    ScreenNames.Permisos.createRoute(Perfil.Empleada)
                ),
                EntradaIndice("Sesión expirada", ScreenNames.SesionExpirada.route),
                EntradaIndice("Aviso sin conexión", ScreenNames.AvisoSinConexion.route),
                EntradaIndice("Exportar", ScreenNames.Exportar.createRoute(Perfil.Responsable)),
            ),
        ),
        SeccionIndice(
            "Empleada/o",
            listOf(
                EntradaIndice("Checar · fuera", ScreenNames.EmpChecar.route),
                EntradaIndice("Checar · dentro", ScreenNames.EmpChecarDentro.route),
                EntradaIndice("Checar · sin internet y fuera de zona", ScreenNames.EmpChecarSinInternet.route),
                EntradaIndice("Mis asistencias", ScreenNames.EmpAsistencias.route),
                EntradaIndice("Mi horario", ScreenNames.EmpHorario.route),
                EntradaIndice("Avisos · bandeja", ScreenNames.EmpAvisos.route),
                EntradaIndice("Aviso · detalle", ScreenNames.EmpAvisoDetalle.route),
            ),
        ),
        SeccionIndice(
            "Responsable y Asistente",
            listOf(
                EntradaIndice("Inicio", ScreenNames.ResInicio.route),
                EntradaIndice("Asistencia del día", ScreenNames.ResAsistencia.route),
                EntradaIndice("Horarios y turnos", ScreenNames.ResHorarios.route),
                EntradaIndice("Personal", ScreenNames.ResPersonal.route),
                EntradaIndice("Horario de la persona", ScreenNames.ResHorarioPersona.route),
                EntradaIndice("Registro por turno completo", ScreenNames.ResRegistroTurno.route),
                EntradaIndice("Registrar ausencia o incidencia", ScreenNames.ResAusencia.createRoute("Paola Reyes")),
                EntradaIndice("Alta y edición de personal", ScreenNames.ResAltaPersonal.route),
                EntradaIndice("Perfil de persona", ScreenNames.ResPerfilPersona.route),
                EntradaIndice("Dar de baja", ScreenNames.ResDarBaja.route),
                EntradaIndice("Avisos · bandeja", ScreenNames.ResAvisos.route),
                EntradaIndice("Nuevo aviso", ScreenNames.ResNuevoAviso.route),
                EntradaIndice("Estado de lectura", ScreenNames.ResEstadoLectura.route),
                EntradaIndice("Menú", ScreenNames.ResMenu.route),
                EntradaIndice("Salas y mínimos", ScreenNames.ResAjustesSalas.route),
                EntradaIndice("Bitácora de la estancia", ScreenNames.ResBitacora.route),
            ),
        ),
        SeccionIndice(
            "Dirección",
            listOf(
                EntradaIndice("Resumen", ScreenNames.DirResumen.route),
                EntradaIndice("Estadísticas", ScreenNames.DirEstadisticas.route),
                EntradaIndice("Bitácora (selector por estancia)", ScreenNames.DirBitacora.route),
            ),
        ),
    )
}
