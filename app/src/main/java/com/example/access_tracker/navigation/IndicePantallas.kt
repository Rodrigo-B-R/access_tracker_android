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
                EntradaIndice("Registro por turno completo", Rutas.ResRegistroTurno),
                EntradaIndice("Registrar ausencia o incidencia", Rutas.ResAusencia),
                EntradaIndice("Alerta de personal insuficiente", Rutas.ResAlertaInsuficiente),
                EntradaIndice("Corregir registro", Rutas.ResCorregir),
                EntradaIndice("Alta y edición de personal", Rutas.ResAltaPersonal),
                EntradaIndice("Perfil de persona", Rutas.ResPerfilPersona),
                EntradaIndice("Dar de baja", Rutas.ResDarBaja),
                EntradaIndice("Avisos · bandeja", Rutas.ResAvisos),
                EntradaIndice("Nuevo aviso", Rutas.ResNuevoAviso),
                EntradaIndice("Estado de lectura", Rutas.ResEstadoLectura),
                EntradaIndice("Menú", Rutas.ResMenu),
                EntradaIndice("Ajustes", Rutas.ResAjustes),
                EntradaIndice("Ajustes · salas y mínimos", Rutas.ResAjustesSalas),
                EntradaIndice("Ajustes · permisos de la Asistente", Rutas.ResAjustesPermisos),
                EntradaIndice("Bitácora de la estancia", Rutas.ResBitacora),
                EntradaIndice("Exportar", Rutas.ResExportar),
            ),
        ),
        SeccionIndice(
            "Dirección",
            listOf(
                EntradaIndice("Resumen", Rutas.DirResumen),
                EntradaIndice("Estancias", Rutas.DirEstancias),
                EntradaIndice("Estadísticas", Rutas.DirEstadisticas),
                EntradaIndice("Bitácora global", Rutas.DirBitacora),
                EntradaIndice("Detalle de estancia", Rutas.DirDetalleEstancia),
                EntradaIndice("Dar de alta estancia", Rutas.DirAltaEstancia),
                EntradaIndice("Bajas y traslados", Rutas.DirBajasTraslados),
                EntradaIndice("Exportar", Rutas.DirExportar),
            ),
        ),
    )
}
