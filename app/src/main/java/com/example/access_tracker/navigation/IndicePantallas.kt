package com.example.access_tracker.navigation

/** Listado de todas las pantallas por perfil, tal como están en el Figma. Solo para la vista de demostración. */
data class EntradaIndice(val titulo: String, val ruta: String)

data class SeccionIndice(val nombre: String, val entradas: List<EntradaIndice>)

object IndicePantallas {
    val secciones = listOf(
        SeccionIndice(
            "Comunes",
            listOf(
                EntradaIndice("Inicio de sesión", Rutas.Login),
                EntradaIndice("Cambio de contraseña inicial", Rutas.CambioPassword),
                EntradaIndice("Permisos de ubicación y notificaciones", Rutas.Permisos),
                EntradaIndice("Sesión expirada", Rutas.SesionExpirada),
                EntradaIndice("Aviso sin conexión", Rutas.AvisoSinConexion),
                EntradaIndice("Exportar", Rutas.Exportar),
            ),
        ),
        SeccionIndice(
            "Empleada/o",
            listOf(
                EntradaIndice("Checar · fuera", Rutas.EmpChecar),
                EntradaIndice("Checar · dentro", Rutas.EmpChecarDentro),
                EntradaIndice("Checar · sin internet y fuera de zona", Rutas.EmpChecarSinInternet),
                EntradaIndice("Mis asistencias", Rutas.EmpAsistencias),
                EntradaIndice("Mi horario", Rutas.EmpHorario),
                EntradaIndice("Avisos · bandeja", Rutas.EmpAvisos),
                EntradaIndice("Aviso · detalle", Rutas.EmpAvisoDetalle),
            ),
        ),
        SeccionIndice(
            "Responsable y Asistente",
            listOf(
                EntradaIndice("Inicio", Rutas.ResInicio),
                EntradaIndice("Asistencia del día", Rutas.ResAsistencia),
                EntradaIndice("Horarios y turnos", Rutas.ResHorarios),
                EntradaIndice("Personal", Rutas.ResPersonal),
                EntradaIndice("Horario de la persona", Rutas.ResHorarioPersona),
                EntradaIndice("Registro por turno completo", Rutas.ResRegistroTurno),
                EntradaIndice("Registrar ausencia o incidencia", Rutas.ResAusencia),
                EntradaIndice("Alerta de personal insuficiente", Rutas.ResAlertaInsuficiente),
                EntradaIndice("Alta y edición de personal", Rutas.ResAltaPersonal),
                EntradaIndice("Perfil de persona", Rutas.ResPerfilPersona),
                EntradaIndice("Dar de baja", Rutas.ResDarBaja),
                EntradaIndice("Avisos · bandeja", Rutas.ResAvisos),
                EntradaIndice("Nuevo aviso", Rutas.ResNuevoAviso),
                EntradaIndice("Estado de lectura", Rutas.ResEstadoLectura),
                EntradaIndice("Menú", Rutas.ResMenu),
                EntradaIndice("Salas y mínimos", Rutas.ResAjustesSalas),
                EntradaIndice("Bitácora de la estancia", Rutas.ResBitacora),
            ),
        ),
        SeccionIndice(
            "Dirección",
            listOf(
                EntradaIndice("Resumen", Rutas.DirResumen),
                EntradaIndice("Estadísticas", Rutas.DirEstadisticas),
                EntradaIndice("Bitácora (selector por estancia)", Rutas.DirBitacora),
            ),
        ),
    )
}
