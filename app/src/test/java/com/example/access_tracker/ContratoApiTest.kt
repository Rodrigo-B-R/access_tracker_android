package com.example.access_tracker

import com.example.access_tracker.model.EstadoChecada
import com.example.access_tracker.model.Horario
import com.example.access_tracker.model.LoginResponse
import com.example.access_tracker.model.MiAsistencia
import com.example.access_tracker.screens.comunes.perfilDeRol
import com.example.access_tracker.navigation.Perfil
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Comprueba que los modelos de la app decodifican respuestas REALES de la API
 * (muestras en src/test/resources/api, tomadas de sonrisas_cristal_api con la cuenta ana.torres).
 * Si la API cambia un campo, este test falla antes que la app.
 */
class ContratoApiTest {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private fun muestra(nombre: String): String =
        javaClass.classLoader!!.getResource("api/$nombre")!!.readText()

    @Test
    fun login() {
        val r = json.decodeFromString(LoginResponse.serializer(), muestra("login.json"))
        assertEquals("employee", r.role)
        assertEquals(Perfil.Empleada, perfilDeRol(r.role))
        assertNotNull(r.user.daycareId)
    }

    @Test
    fun estadoDeChecada() {
        val r = json.decodeFromString(EstadoChecada.serializer(), muestra("status.json"))
        assertNull(r.openRecord)
        assertEquals(150, r.location.radiusMeters)
    }

    @Test
    fun horarioPropio() {
        val h = json.decodeFromString(Horario.serializer().nullable, muestra("schedule_me.json"))
        assertNotNull(h)
        assertEquals("monday", h!!.days.first().weekday)
        assertEquals("07:00", h.days.first().shift.startTime)
    }

    @Test
    fun horarioNuloSiNoHayHorario() {
        assertNull(json.decodeFromString(Horario.serializer().nullable, "null"))
    }

    @Test
    fun asistenciaPropia() {
        val a = json.decodeFromString(MiAsistencia.serializer(), muestra("attendance_me.json"))
        assertEquals(4, a.summary.absences)
        assertEquals("pending", a.days.first().status)
    }

    @Test
    fun roles() {
        assertEquals(Perfil.Responsable, perfilDeRol("manager"))
        assertEquals(Perfil.Direccion, perfilDeRol("director"))
        assertNull(perfilDeRol("otra-cosa"))
    }
}
