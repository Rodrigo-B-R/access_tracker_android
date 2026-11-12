package com.example.access_tracker

import com.example.access_tracker.model.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assume.assumeTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas de integracion SOLO LECTURA contra la API real, con los DTOs de la app.
 * Se saltan salvo que exista la variable de entorno API_IT=1 (API_URL opcional, por defecto localhost:8000).
 * Uso: API_IT=1 ./gradlew testDebugUnitTest --tests "*IntegracionApiTest*"
 */
class IntegracionApiTest {
    private val base = System.getenv("API_URL") ?: "http://localhost:8000"
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val http = OkHttpClient()
    private fun pass(u: String) = "Cristal2026"

    private fun login(usuario: String): LoginResponse {
        assumeTrue("Define API_IT=1 para correr", System.getenv("API_IT") == "1")
        val body = json.encodeToString(LoginRequest.serializer(), LoginRequest(usuario, pass(usuario)))
        val req = Request.Builder().url("$base/login")
            .post(body.toRequestBody("application/json".toMediaType())).build()
        http.newCall(req).execute().use {
            assertEquals("login $usuario: ${it.code}", 200, it.code)
            return json.decodeFromString(LoginResponse.serializer(), it.body!!.string())
        }
    }

    private fun <T> get(token: String, path: String, ser: KSerializer<T>): T {
        val req = Request.Builder().url("$base$path").header("Authorization", "Bearer $token").build()
        http.newCall(req).execute().use {
            val texto = it.body!!.string()
            assertEquals("GET $path -> ${it.code}: ${texto.take(200)}", 200, it.code)
            return try {
                json.decodeFromString(ser, texto)
            } catch (e: Exception) {
                throw AssertionError("GET $path no decodifica: ${e.message}\n${texto.take(400)}", e)
            }
        }
    }

    private val hoy = LocalDate.now()

    @Test
    fun empleada() {
        val l = login("ana.torres")
        assertEquals("employee", l.role)
        val t = l.token
        val perfil = get(t, "/profile", MiPerfil.serializer())
        assertEquals("employee", perfil.role?.name)
        assertTrue(perfil.daycare?.name?.isNotBlank() == true)
        get(t, "/time-records/status", EstadoChecada.serializer())
        get(t, "/schedules/me", Horario.serializer().nullable)
        val a = get(t, "/attendance/me?startDate=${hoy.minusDays(7)}&endDate=$hoy", MiAsistencia.serializer())
        assertTrue(a.days.isNotEmpty())
        get(t, "/notices/inbox", ListSerializer(Aviso.serializer()))
    }

    @Test
    fun responsable() {
        val l = login("marisol.garcia")
        assertEquals("manager", l.role)
        val t = l.token
        get(t, "/coverage", Cobertura.serializer())
        get(t, "/catalogs", Catalogos.serializer())
        get(t, "/attendance?startDate=$hoy&endDate=$hoy", ListSerializer(AsistenciaPersona.serializer()))
        get(t, "/schedules", ListSerializer(HorarioDePersona.serializer()))
        val staff = get(t, "/staff?active=true", ListSerializer(Persona.serializer()))
        get(t, "/staff?active=false", ListSerializer(Persona.serializer()))
        assertTrue(staff.isNotEmpty())
        val id = staff.first().id
        get(t, "/staff/$id", PersonaDetalle.serializer())
        get(t, "/schedules/$id", HorarioDePersona.serializer())
        get(t, "/attendance/$id?startDate=${hoy.withDayOfMonth(1)}&endDate=$hoy", AsistenciaPersona.serializer())
        get(t, "/rooms", ListSerializer(Sala.serializer()))
        val avisos = get(t, "/notices", ListSerializer(AvisoGestion.serializer()))
        get(t, "/notices/inbox", ListSerializer(Aviso.serializer()))
        avisos.firstOrNull()?.let { get(t, "/notices/${it.id}", AvisoDetalleGestion.serializer()) }
        get(t, "/movements?startDate=${hoy.minusDays(90)}&endDate=$hoy", ListSerializer(MovimientoDetalle.serializer()))
        get(t, "/daycares", ListSerializer(Estancia.serializer()))
    }

    @Test
    fun direccion() {
        val l = login("direccion")
        assertEquals("director", l.role)
        val t = l.token
        get(t, "/summary?date=$hoy", ResumenDireccion.serializer())
        get(t, "/statistics?startDate=${hoy.minusDays(30)}&endDate=$hoy", Estadisticas.serializer())
        get(t, "/audit?startDate=${hoy.minusDays(14)}&endDate=$hoy", ListSerializer(EventoAuditoria.serializer()))
        get(t, "/daycares", ListSerializer(Estancia.serializer()))
    }
}
