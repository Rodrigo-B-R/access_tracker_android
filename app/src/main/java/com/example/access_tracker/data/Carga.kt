package com.example.access_tracker.data

/** Estado de una pantalla que carga datos de la API. */
sealed interface Carga<out T> {
    data object Cargando : Carga<Nothing>
    data class Error(val mensaje: String) : Carga<Nothing>
    data class Listo<T>(val datos: T) : Carga<T>
}

/** Mensaje en espanol para mostrar al usuario (los de la API vienen en ingles). */
fun ApiError.texto(): String = when (this) {
    ApiError.SinConexion -> "Sin conexión con el servidor. Revisa tu internet."
    ApiError.NoAutorizado -> "Tu sesión ya no es válida."
    is ApiError.Limitado -> "Demasiadas peticiones. Espera un momento."
    is ApiError.Servidor -> "El servidor tuvo un problema ($codigo). Intenta más tarde."
}

fun <T> ApiResult<T>.aCarga(): Carga<T> = when (this) {
    is ApiResult.Ok -> Carga.Listo(value)
    is ApiResult.Error -> Carga.Error(error.texto())
}
