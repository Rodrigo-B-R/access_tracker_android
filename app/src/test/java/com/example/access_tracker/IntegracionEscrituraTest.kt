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
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Escrituras contra la API real con una persona de prueba propia ("zz.prueba.<ts>"), dada de baja al final.
 * Deja rastro en la bitacora (la API no permite borrar). Solo corre con API_IT=1.
 */
class IntegracionEscrituraTest {
    private val base = System.getenv("API_URL") ?: "http://localhost:8000"
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val http = OkHttpClient()
    private val media = "application/json".toMediaType()

    private fun login(u: String, p: String): LoginResponse {
        val body = json.encodeToString(LoginRequest.serializer(), LoginRequest(u, p))
        http.newCall(Request.Builder().url("$base/login").post(body.toRequestBody(media)).build()).execute().use {
            val t = it.body!!.string()
            assertEquals("login $u: ${it.code} $t", 200, it.code)
            return json.decodeFromString(LoginResponse.serializer(), t)
        }
    }

    private fun <T> call(t: String, metodo: String, path: String, cuerpo: String?, ser: KSerializer<T>, esperado: Int = 200): T? {
        val rb = (cuerpo ?: "{}").toRequestBody(media).takeIf { metodo != "GET" }
        val req = Request.Builder().url("$base$path").header("Authorization", "Bearer $t").method(metodo, rb).build()
        http.newCall(req).execute().use {
            val txt = it.body!!.string()
            assertTrue("$metodo $path -> ${it.code} (esperado $esperado): ${txt.take(300)}", it.code == esperado || (esperado == 200 && it.code == 201))
            return if (it.code in 200..299) try { json.decodeFromString(ser, txt) } catch (e: Exception) {
                throw AssertionError("$metodo $path no decodifica: ${e.message}\n${txt.take(400)}", e)
            } else null
        }
    }

    @Test
    fun flujoResponsable() {
        assumeTrue("Define API_IT=1", System.getenv("API_IT") == "1")
        val m = login("marisol.garcia", "Cristal2026").token
        val cat = call(m, "GET", "/catalogs", null, Catalogos.serializer())!!
        val sala = call(m, "GET", "/rooms", null, ListSerializer(Sala.serializer()))!!.first()
        val turno = cat.shifts.first()
        val ts = System.currentTimeMillis() % 100000000
        val user = "zz.prueba.$ts"

        // Alta
        val alta = AltaPersonaRequest("Prueba Integracion $ts", user, "$user@example.com", "8112345678", cat.positions.first().id)
        val creada = call(m, "POST", "/staff", json.encodeToString(AltaPersonaRequest.serializer(), alta), PersonaCreada.serializer(), 201)!!
        val id = creada.id
        try {
            // La persona nueva puede entrar con la contraseña temporal
            login(user, creada.temporaryPassword)

            // Edicion
            val ed = EdicionPersonaRequest(phone = "8187654321")
            call(m, "PUT", "/staff/$id", json.encodeToString(EdicionPersonaRequest.serializer(), ed), Persona.serializer())
            val det = call(m, "GET", "/staff/$id", null, PersonaDetalle.serializer())!!
            assertEquals("8187654321", det.phone)

            // Restablecer contraseña
            val nueva = call(m, "POST", "/staff/$id/password", "{}", PasswordTemporal.serializer())!!
            login(user, nueva.temporaryPassword)
            val e = login(user, nueva.temporaryPassword).token

            // Horario (lunes y martes)
            val hr = HorarioRequest(days = listOf("monday", "tuesday").map { DiaHorarioRequest(it, turno.id, sala.id) })
            call(m, "PUT", "/schedules/$id", json.encodeToString(HorarioRequest.serializer(), hr), Horario.serializer())
            val h = call(e, "GET", "/schedules/me", null, Horario.serializer().nullable)
            assertNotNull(h)

            // Sala: PUT sin cambio real
            call(m, "PUT", "/rooms/${sala.id}", json.encodeToString(SalaRequest.serializer(), SalaRequest(minStaff = sala.minStaff)), Sala.serializer())

            // Aviso solo para la persona de prueba, con confirmacion
            val av = AvisoNuevo("Aviso de prueba $ts", "Mensaje de prueba", "reminder", target = "people", userIds = listOf(id), requiresConfirmation = true)
            val creado = call(m, "POST", "/notices", json.encodeToString(AvisoNuevo.serializer(), av), AvisoGestion.serializer(), 201)!!
            val inbox = call(e, "GET", "/notices/inbox", null, ListSerializer(Aviso.serializer()))!!
            assertTrue(inbox.any { it.id == creado.id })
            call(e, "POST", "/notices/${creado.id}/read", "{}", AcuseLectura.serializer())
            val d = call(m, "GET", "/notices/${creado.id}", null, AvisoDetalleGestion.serializer())!!
            assertEquals(1, d.confirmed.size)

            // Ausencia en una fecha pasada (hace 30 dias)
            val fecha = LocalDate.now().minusDays(30).toString()
            val aus = AsistenciaRequest(id, fecha, "absent", cat.absenceReasons.firstOrNull()?.id)
            call(m, "POST", "/attendance", json.encodeToString(AsistenciaRequest.serializer(), aus), AsistenciaRegistrada.serializer(), 201)

            // Traslado no se prueba (cambia de estancia). Rechazo de validacion: alta duplicada -> 409/400
            val dup = http.newCall(Request.Builder().url("$base/staff").header("Authorization", "Bearer $m")
                .post(json.encodeToString(AltaPersonaRequest.serializer(), alta).toRequestBody(media)).build()).execute()
            dup.use { assertTrue("duplicado debia fallar: ${it.code}", it.code in 400..409) }
        } finally {
            val baja = BajaRequest(cat.terminationReasons.first().id, "Limpieza de prueba de integracion")
            call(m, "POST", "/staff/$id/terminate", json.encodeToString(BajaRequest.serializer(), baja), Persona.serializer())
        }
    }
}
