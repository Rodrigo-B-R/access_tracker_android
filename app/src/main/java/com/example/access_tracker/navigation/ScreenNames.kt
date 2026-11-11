package com.example.access_tracker.navigation

sealed class ScreenNames(val route: String) {
    // Comunes
    data object Indice : ScreenNames("indice")
    data object Login : ScreenNames("login")
    data object CambioPassword : ScreenNames("cambio_password/{perfil}") {
        fun createRoute(perfil: Perfil) = "cambio_password/${perfil.name}"
    }
    data object Permisos : ScreenNames("permisos/{perfil}") {
        fun createRoute(perfil: Perfil) = "permisos/${perfil.name}"
    }
    data object SesionExpirada : ScreenNames("sesion_expirada")
    data object AvisoSinConexion : ScreenNames("aviso_sin_conexion")
    data object Exportar : ScreenNames("exportar/{perfil}") {
        fun createRoute(perfil: Perfil) = "exportar/${perfil.name}"
    }

    // Empleada
    data object EmpChecar : ScreenNames("emp_checar")
    data object EmpChecarDentro : ScreenNames("emp_checar_dentro")
    data object EmpChecarSinInternet : ScreenNames("emp_checar_sin_internet")
    data object EmpAsistencias : ScreenNames("emp_asistencias")
    data object EmpHorario : ScreenNames("emp_horario")
    data object EmpAvisos : ScreenNames("emp_avisos")
    data object EmpAvisoDetalle : ScreenNames("emp_aviso_detalle") // demo del indice
    data object EmpAviso : ScreenNames("emp_aviso/{id}") {
        fun createRoute(id: Long) = "emp_aviso/$id"
    }

    // Responsable y Asistente
    data object ResInicio : ScreenNames("res_inicio")
    data object ResAsistencia : ScreenNames("res_asistencia")
    data object ResHorarios : ScreenNames("res_horarios")
    data object ResPersonal : ScreenNames("res_personal")
    data object ResHorarioPersona : ScreenNames("res_horario_persona/{id}") {
        fun createRoute(id: Long) = "res_horario_persona/$id"
    }
    data object ResRegistroTurno : ScreenNames("res_registro_turno")
    /** id = 0 significa "sin persona elegida". */
    data object ResAusencia : ScreenNames("res_ausencia/{id}") {
        fun createRoute(id: Long?) = "res_ausencia/${id ?: 0}"
    }
    data object ResAltaPersonal : ScreenNames("res_alta_personal")
    data object ResPerfilPersona : ScreenNames("res_perfil_persona/{id}") {
        fun createRoute(id: Long) = "res_perfil_persona/$id"
    }
    data object ResEditarPersonal : ScreenNames("res_editar_personal/{id}") {
        fun createRoute(id: Long) = "res_editar_personal/$id"
    }
    data object ResDarBaja : ScreenNames("res_dar_baja/{id}") {
        fun createRoute(id: Long) = "res_dar_baja/$id"
    }
    data object ResAvisos : ScreenNames("res_avisos")
    data object ResNuevoAviso : ScreenNames("res_nuevo_aviso")
    data object ResEstadoLectura : ScreenNames("res_estado_lectura/{id}") {
        fun createRoute(id: Long) = "res_estado_lectura/$id"
    }
    data object ResMenu : ScreenNames("res_menu")
    data object ResAjustesSalas : ScreenNames("res_ajustes_salas")
    data object ResBitacora : ScreenNames("res_bitacora")

    // Dirección
    data object DirResumen : ScreenNames("dir_resumen")
    data object DirEstadisticas : ScreenNames("dir_estadisticas")
    data object DirBitacora : ScreenNames("dir_bitacora")
}
