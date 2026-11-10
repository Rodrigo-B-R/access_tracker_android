package com.example.access_tracker.data

/**
 * Reglas de la contrasena nueva. Reflejan password_problems() de la API (app/security.py):
 * la API es la autoridad; esto solo da respuesta inmediata mientras se escribe.
 */
object ReglasPassword {
    const val MIN_CARACTERES = 8
    const val MAX_BYTES = 72 // limite de bcrypt

    data class Requisito(val texto: String, val cumplido: Boolean)

    fun requisitos(nueva: String, actual: String, confirmacion: String): List<Requisito> = listOf(
        Requisito("Al menos $MIN_CARACTERES caracteres", nueva.length >= MIN_CARACTERES),
        Requisito("Una mayúscula y una minúscula", nueva.any { it.isUpperCase() } && nueva.any { it.isLowerCase() }),
        Requisito("Un número", nueva.any { it.isDigit() }),
        Requisito("Distinta a la actual", nueva.isNotEmpty() && nueva != actual),
        Requisito("Las dos contraseñas coinciden", nueva.isNotEmpty() && nueva == confirmacion),
    )

    fun valida(nueva: String, actual: String, confirmacion: String): Boolean =
        actual.isNotEmpty() &&
            nueva.toByteArray(Charsets.UTF_8).size <= MAX_BYTES &&
            requisitos(nueva, actual, confirmacion).all { it.cumplido }
}
