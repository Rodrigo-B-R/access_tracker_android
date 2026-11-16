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
import com.example.access_tracker.data.local.ChecadaDao
import com.example.access_tracker.data.local.ChecadaLocal
import com.example.access_tracker.data.local.EstadoEnvio
import com.example.access_tracker.model.EstadoChecada
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

/** Modo offline: la cola persistente y la decision de que hacer con cada respuesta de la API. */
class ColaOfflineTest {
    private class AlmacenEnMemoria : Almacen {
        val datos = mutableMapOf<String, String>()
        override fun leer(clave: String) = datos[clave]
        override fun escribir(clave: String, valor: String?) {
            if (valor == null) datos.remove(clave) else datos[clave] = valor
        }
    }

    /** Imita en memoria lo que hace Room con las consultas de [ChecadaDao]. */
    private class DaoEnMemoria : ChecadaDao {
        val filas = mutableListOf<ChecadaLocal>()
        private var siguienteId = 1L

        private fun cambiar(cond: (ChecadaLocal) -> Boolean, f: (ChecadaLocal) -> ChecadaLocal) {
            filas.replaceAll { if (cond(it)) f(it) else it }
        }

        override suspend fun insertar(checada: ChecadaLocal): Long {
            if (filas.any { it.uuid == checada.uuid && it.tipo == checada.tipo }) return -1 // IGNORE
            val fila = checada.copy(id = siguienteId++)
            filas += fila
            return fila.id
        }

        override suspend fun pendientes(usuarioId: Long) =
            filas.filter { it.usuarioId == usuarioId && it.estado == EstadoEnvio.PENDIENTE }.sortedBy { it.id }

        override suspend fun hayPendientes() = filas.any { it.estado == EstadoEnvio.PENDIENTE }

        override suspend fun marcarEnviada(uuid: String, tipo: String, ahora: Long) = cambiar(
            { it.uuid == uuid && it.tipo == tipo && it.estado == EstadoEnvio.PENDIENTE },
            { it.copy(estado = EstadoEnvio.ENVIADA, procesadoEn = ahora) },
        )

        override suspend fun marcarRechazada(uuid: String, tipo: String, motivo: String, ahora: Long) = cambiar(
            { it.uuid == uuid && it.tipo == tipo && it.estado == EstadoEnvio.PENDIENTE },
            { it.copy(estado = EstadoEnvio.RECHAZADA, motivoRechazo = motivo, procesadoEn = ahora) },
        )

        override suspend fun sumarIntento(uuid: String, tipo: String) = cambiar(
            { it.uuid == uuid && it.tipo == tipo }, { it.copy(intentos = it.intentos + 1) },
        )

        override suspend fun rechazosSinAviso(usuarioId: Long) = filas.filter {
            it.usuarioId == usuarioId && it.estado == EstadoEnvio.RECHAZADA && !it.avisoMostrado
        }.sortedBy { it.id }

        override suspend fun marcarAvisosMostrados(usuarioId: Long) = cambiar(
            { it.usuarioId == usuarioId && it.estado == EstadoEnvio.RECHAZADA }, { it.copy(avisoMostrado = true) },
        )

        override suspend fun borrarTerminadasAntesDe(antesDe: Long): Int {
            val antes = filas.size
            filas.removeAll {
                (it.procesadoEn ?: Long.MAX_VALUE) < antesDe &&
                    (it.estado == EstadoEnvio.ENVIADA || (it.estado == EstadoEnvio.RECHAZADA && it.avisoMostrado))
            }
            return antes - filas.size
        }
    }

    private fun entrada(uuid: String, usuario: Long = 3, hora: String = "2026-11-09T13:01:00.000Z") =
        Pendiente(uuid, Pendiente.TIPO_ENTRADA, usuario, hora, latitud = 25.7447, longitud = -100.4036)

    private fun salida(uuid: String, usuario: Long = 3, hora: String = "2026-11-09T21:02:00.000Z") =
        Pendiente(uuid, Pendiente.TIPO_SALIDA, usuario, hora)

    private val dia = TimeUnit.DAYS.toMillis(1)

    // ── Cola ────────────────────────────────────────────────────────

    @Test
    fun conservaElOrdenDeLlegada() = runBlocking {
        val cola = ColaChecadas(DaoEnMemoria())
        cola.agregar(entrada("a"))
        cola.agregar(salida("b"))
        assertEquals(listOf("a", "b"), cola.pendientes(3).map { it.uuid })
        assertEquals(listOf(Pendiente.TIPO_ENTRADA, Pendiente.TIPO_SALIDA), cola.pendientes(3).map { it.tipo })
    }

    @Test
    fun sobreviveAReiniciarLaApp() = runBlocking {
        val dao = DaoEnMemoria()
        ColaChecadas(dao).agregar(entrada("a"))
        // Otra instancia sobre la misma base = la app abierta de nuevo.
        val despues = ColaChecadas(dao).pendientes(3)
        assertEquals(1, despues.size)
        assertEquals(25.7447, despues.first().latitud!!, 0.0)
        assertEquals("2026-11-09T13:01:00.000Z", despues.first().ocurridoEn)
    }

    @Test
    fun noDuplicaLaMismaChecada() = runBlocking {
        val cola = ColaChecadas(DaoEnMemoria())
        cola.agregar(entrada("a"))
        cola.agregar(entrada("a"))
        assertEquals(1, cola.pendientes(3).size)
    }

    @Test
    fun cadaPersonaSoloVeSusChecadas() = runBlocking {
        val cola = ColaChecadas(DaoEnMemoria())
        cola.agregar(entrada("a", usuario = 3))
        cola.agregar(entrada("b", usuario = 9))
        assertEquals(listOf("a"), cola.pendientes(3).map { it.uuid })
        assertEquals(listOf("b"), cola.pendientes(9).map { it.uuid })
        assertTrue(cola.hayPendientes())
    }

    @Test
    fun enviadaYaNoEsPendientePeroSeConserva() = runBlocking {
        val dao = DaoEnMemoria()
        val cola = ColaChecadas(dao)
        cola.agregar(entrada("a"))
        cola.agregar(salida("b"))
        cola.marcarEnviada("a", Pendiente.TIPO_ENTRADA)
        assertEquals(listOf("b"), cola.pendientes(3).map { it.uuid })
        cola.marcarEnviada("b", Pendiente.TIPO_SALIDA)
        assertFalse(cola.hayPendientes())
        // Siguen en la tabla como ENVIADA (no se vuelven a mandar).
        assertEquals(listOf(EstadoEnvio.ENVIADA, EstadoEnvio.ENVIADA), dao.filas.map { it.estado })
    }

    @Test
    fun rechazadaSeAvisaUnaSolaVezPorUsuario() = runBlocking {
        val cola = ColaChecadas(DaoEnMemoria())
        cola.agregar(entrada("a", usuario = 3))
        cola.agregar(salida("b", usuario = 9))
        cola.marcarRechazada("a", Pendiente.TIPO_ENTRADA, "motivo")
        cola.marcarRechazada("b", Pendiente.TIPO_SALIDA, "motivo")
        assertEquals(listOf("motivo"), cola.rechazos(3).map { it.motivo })
        assertFalse(cola.hayPendientes())
        cola.limpiarRechazos(3)
        assertEquals(0, cola.rechazos(3).size)
        assertEquals(1, cola.rechazos(9).size)
    }

    @Test
    fun limpiezaBorraTerminadasViejasYNuncaPendientes() = runBlocking {
        var ahora = 0L
        val dao = DaoEnMemoria()
        val cola = ColaChecadas(dao) { ahora }
        cola.agregar(entrada("enviada"))
        cola.agregar(entrada("rechazada-vista"))
        cola.agregar(entrada("rechazada-sin-ver"))
        cola.agregar(entrada("pendiente"))
        cola.marcarEnviada("enviada", Pendiente.TIPO_ENTRADA)
        cola.marcarRechazada("rechazada-vista", Pendiente.TIPO_ENTRADA, "m")
        cola.marcarRechazada("rechazada-sin-ver", Pendiente.TIPO_ENTRADA, "m")
        dao.marcarAvisosMostrados(3)
        dao.filas.replaceAll { if (it.uuid == "rechazada-sin-ver") it.copy(avisoMostrado = false) else it }

        ahora = 6 * dia
        assertEquals(0, cola.limpiarViejas()) // todavia no pasan 7 dias

        ahora = 8 * dia
        assertEquals(2, cola.limpiarViejas())
        assertEquals(listOf("rechazada-sin-ver", "pendiente"), dao.filas.map { it.uuid })
    }

    @Test
    fun migraLaColaViejaDeSharedPreferences() = runBlocking {
        val json = Json
        val viejo = AlmacenEnMemoria().apply {
            datos["pendientes"] = json.encodeToString(
                ListSerializer(Pendiente.serializer()), listOf(entrada("a"), salida("b")),
            )
            datos["rechazos"] = json.encodeToString(
                ListSerializer(Rechazo.serializer()),
                listOf(Rechazo("entrada", "2026-11-08T13:00:00.000Z", "motivo", 3)),
            )
        }
        val dao = DaoEnMemoria()
        val cola = ColaChecadas(dao, viejo)
        assertEquals(listOf("a", "b"), cola.pendientes(3).map { it.uuid })
        assertEquals(1, cola.rechazos(3).size)
        assertTrue(viejo.datos.isEmpty()) // el almacen viejo queda vacio

        // Abrir la app otra vez no duplica nada.
        ColaChecadas(dao, viejo).pendientes(3)
        assertEquals(3, dao.filas.size)
    }

    @Test
    fun datosViejosCorruptosNoTumbanLaApp() = runBlocking {
        val viejo = AlmacenEnMemoria().apply { datos["pendientes"] = "{esto no es json" }
        assertEquals(emptyList<Pendiente>(), ColaChecadas(DaoEnMemoria(), viejo).pendientes(3))
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
