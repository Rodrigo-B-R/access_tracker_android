package com.example.access_tracker.screens.comunes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.access_tracker.data.ApiError
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.ServiceLocator
import com.example.access_tracker.data.texto
import com.example.access_tracker.model.CambioPasswordRequest
import com.example.access_tracker.model.MensajeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CambioPasswordEstado(
    val enviando: Boolean = false,
    val error: String? = null,
    /** true cuando la API acepto el cambio; la UI cierra la pantalla y llama a [consumirListo]. */
    val listo: Boolean = false,
)

class CambioPasswordViewModel : ViewModel() {
    private val _estado = MutableStateFlow(CambioPasswordEstado())
    val estado: StateFlow<CambioPasswordEstado> = _estado.asStateFlow()

    fun guardar(actual: String, nueva: String) {
        if (_estado.value.enviando) return
        _estado.value = CambioPasswordEstado(enviando = true)
        viewModelScope.launch {
            val r = ServiceLocator.api.put(
                "/profile/password",
                CambioPasswordRequest(actual, nueva),
                CambioPasswordRequest.serializer(),
                MensajeApi.serializer(),
            )
            _estado.update {
                when (r) {
                    is ApiResult.Ok -> CambioPasswordEstado(listo = true)
                    is ApiResult.Error -> CambioPasswordEstado(error = mensaje(r.error))
                }
            }
        }
    }

    fun consumirListo() = _estado.update { it.copy(listo = false) }

    private fun mensaje(e: ApiError): String = when {
        e is ApiError.Servidor && e.codigo == 401 -> "La contraseña actual no es correcta."
        // 400: la API rechaza la nueva (los requisitos ya se validan en pantalla; esto es la red de seguridad).
        e is ApiError.Servidor && e.codigo == 400 -> "La contraseña nueva no cumple los requisitos."
        e is ApiError.Limitado -> "Demasiados intentos. Espera un momento."
        else -> e.texto()
    }
}
