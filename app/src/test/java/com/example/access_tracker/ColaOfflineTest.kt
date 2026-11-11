package com.example.access_tracker

import com.example.access_tracker.data.Almacen
import com.example.access_tracker.data.ApiError
import com.example.access_tracker.data.ApiResult
import com.example.access_tracker.data.CacheEstado
import com.example.access_tracker.data.ColaChecadas
import com.example.access_tracker.data.Decision
import com.example.access_tracker.data.Pendiente
import com.example.access_tracker.data.Rechazo
import com.example.access_tracker.data.decidir
import com.example.access_tracker.model.EstadoChecada
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Modo offline: la cola persistente y la decision de que hacer con cada respuesta de la API. */
class ColaOfflineTest {
    private class AlmacenEnMemoria : Almacen {
        val datos = mutableMapOf<String, String>()
        override fun leer(clave: String) = datos[clave]
        override fun escribir(clave: String, valor: String?) {
            if (valor == null) datos.remove(clave) else datos[clave] = valor
        }
    }

    private fun entrada(uuid: String, usuario: Long = 3, hora: String = "2026-11-09T13:01:00.000Z") =
        Pendiente(uuid, Pendiente.TIPO_ENTRADA, usuario, hora, latitud = 25.7447, longitud = -100.4036)

    private fun salida(uuid: String, usuario: Long = 3, hora: String = "2026-11-09T21:02:00.000Z") =
        Pendiente(uuid, Pendiente.TIPO_SALIDA, usuario, hora)

    // ── Cola ────────────────────────────────────────────────────────

    @Test
    fun conservaElOrdenDeLlegada() {
        val cola = ColaChecadas(AlmacenEnMemoria())
        cola.agregar(entrada("a"))
        cola.agregar(salida("b"))
        assertEquals(listOf("a", "b"), cola.pendientes(3).map { it.uuid })
        assertEquals(listOf(Pendiente.TIPO_ENTRADA, Pendiente.TIPO_SALIDA), cola.pendientes(3).map { it.tipo })
    }

    @Test
    fun sobreviveAReiniciarLaApp() {
        val almacen = AlmacenEnMemoria()
        ColaChecadas(almacen).agregar(entrada("a"))
        // Otra instancia sobre el mismo almacen = la app abierta de nuevo.
        val despues = ColaChecadas(almacen).pendientes(3)
        assertEquals(1, despues.size)
        assertEquals(25.7447, despues.first().latitud!!, 0.0)
        assertEquals("2026-11-09T13:01:00.000Z", despues.first().ocurridoEn)
    }

    @Test
    fun cadaPersonaSoloVeSusChecadas() {
        val cola = ColaChecadas(AlmacenEnMemoria())
        cola.agregar(entrada("a", usuario = 3))
        cola.agregar(entrada("b", usuario = 9))
        assertEquals(listOf("a"), cola.pendientes(3).map { it.uuid })
        assertEquals(listOf("b"), cola.pendientes(9).map { it.uuid })
        assertTrue(cola.hayPendientes())
    }

    @Test
    fun quitarSoloQuitaLaIndicada() {
        val cola = ColaChecadas(AlmacenEnMemoria())
        cola.agregar(entrada("a"))
        cola.agregar(salida("b"))
        cola.quitar("a", Pendiente.TIPO_ENTRADA)
        assertEquals(listOf("b"), cola.pendientes(3).map { it.uuid })
        cola.quitar("b", Pendiente.TIPO_SALIDA)
        assertTrue(!cola.hayPendientes())
    }

    @Test
    fun datosCorruptosNoTumbanLaApp() {
        val almacen = AlmacenEnMemoria().apply { datos["pendientes"] = "{esto no es json" }
        assertEquals(emptyList<Pendiente>(), ColaChecadas(almacen).pendientes(3))
    }

    @Test
    fun rechazosSePuedenLeerYLimpiarPorUsuario() {
        val cola = ColaChecadas(AlmacenEnMemoria())
        cola.agregarRechazo(Rechazo("entrada", "2026-11-09T13:01:00.000Z", "motivo", 3))
        cola.agregarRechazo(Rechazo("salida", "2026-11-09T21:00:00.000Z", "motivo", 9))
        assertEquals(1, cola.rechazos(3).size)
        cola.limpiarRechazos(3)
        assertEquals(0, cola.rechazos(3).size)
        assertEquals(1, cola.rechazos(9).size)
    }

    // ── Que hacer con cada respuesta de la API ──────────────────────

    @Test
    fun exitoSeQuitaDeLaCola() {
        assertEquals(Decision.Enviada, decidir(Pendiente.TIPO_ENTRADA, ApiResult.Ok("x")))
    }

    @Test
    fun sinRedSeReintenta() {
        assertEquals(Decision.Reintentar, decidir(Pendiente.TIPO_ENTRADA, ApiResult.Error(ApiError.SinConexion)))
        assertEquals(Decision.Reintentar, decidir(Pendiente.TIPO_SALIDA, ApiResult.Error(ApiError.Limitado(30))))
        assertEquals(Decision.Reintentar, decidir(Pendiente.TIPO_SALIDA, ApiResult.Error(ApiError.Servidor(500, null))))
        assertEquals(Decision.Reintentar, decidir(Pendiente.TIPO_ENTRADA, ApiResult.Error(ApiError.NoAutorizado)))
    }

    @Test
    fun conflicto409CuentaComoEnviado() {
        // "Ya hay entrada abierta" / "no hay entrada abierta": el estado que se queria ya existe.
        assertEquals(Decision.Enviada, decidir(Pendiente.TIPO_ENTRADA, ApiResult.Error(ApiError.Servidor(409, "x"))))
        assertEquals(Decision.Enviada, decidir(Pendiente.TIPO_SALIDA, ApiResult.Error(ApiError.Servidor(409, "x"))))
    }

    @Test
    fun rechazoDefinitivoSeDescartaYSeAvisa() {
        val d = decidir(Pendiente.TIPO_ENTRADA, ApiResult.Error(ApiError.Servidor(400, "no schedule")))
        assertTrue(d is Decision.Descartar)
        assertTrue((d as Decision.Descartar).motivo.isNotBlank())
    }

    // ── Copia del estado ────────────────────────────────────────────

    @Test
    fun copiaDelEstadoSoloLaVeSuDueno() {
        val json = Json { ignoreUnknownKeys = true }
        val estado = json.decodeFromString(
            EstadoChecada.serializer(),
            javaClass.classLoader!!.getResource("api/status.json")!!.readText(),
        )
        val cache = CacheEstado(AlmacenEnMemoria())
        cache.guardar(3, "2026-11-09", estado)
        assertEquals("2026-11-09", cache.leer(3)!!.fecha)
        assertEquals(150, cache.leer(3)!!.estado.location.radiusMeters)
        assertNull(cache.leer(9)) // otra persona en el mismo telefono: nada
    }
}
