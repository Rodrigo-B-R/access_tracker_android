package com.example.access_tracker.screens.comunes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.access_tracker.data.ApiError
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.navigation.Perfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Rol de la API ("employee" | "manager" | "director") -> perfil de la app. */
fun perfilDeRol(rol: String): Perfil? = when (rol) {
    "employee" -> Perfil.Empleada
    "manager" -> Perfil.Responsable
    "director" -> Perfil.Direccion
    else -> null
}

data class LoginEstado(
    val cargando: Boolean = false,
    val error: String? = null,
    /** Se llena cuando el login sale bien; la UI navega y llama a [AuthViewModel.consumirPerfil]. */
    val perfil: Perfil? = null,
)

class AuthViewModel : ViewModel() {
    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun iniciarSesion(usuario: String, password: String) {
        if (_estado.value.cargando) return
        if (usuario.isBlank() || password.isEmpty()) {
            _estado.update { it.copy(error = "Escribe tu clave y tu contraseña") }
            return
        }
        _estado.update { LoginEstado(cargando = true) }
        viewModelScope.launch {
            when (val r = ServiceLocator.api.login(usuario, password)) {
                is ApiResult.Ok -> {
                    val perfil = perfilDeRol(r.value.role)
                    if (perfil == null) {
                        _estado.update { LoginEstado(error = "Tu cuenta tiene un rol desconocido") }
                    } else {
                        ServiceLocator.sesion.guardar(r.value)
                        _estado.update { LoginEstado(perfil = perfil) }
                    }
                }
                is ApiResult.Error -> _estado.update { LoginEstado(error = mensaje(r.error)) }
            }
        }
    }

    fun consumirPerfil() = _estado.update { it.copy(perfil = null) }

    private fun mensaje(e: ApiError): String = when (e) {
        ApiError.SinConexion -> "No hay conexión con el servidor. Revisa tu internet."
        ApiError.NoAutorizado -> "Clave o contraseña incorrecta"
        is ApiError.Limitado -> {
            val min = e.reintentarEnSegundos?.let { (it + 59) / 60 }
            if (min != null) "Demasiados intentos. Prueba de nuevo en $min min."
            else "Demasiados intentos. Prueba más tarde."
        }
        is ApiError.Servidor -> "El servidor tuvo un problema (${e.codigo}). Intenta más tarde."
    }
}
