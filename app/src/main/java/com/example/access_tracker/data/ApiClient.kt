package com.example.access_tracker.data

import android.content.Context
import com.example.access_tracker.BuildConfig
import com.example.access_tracker.model.LoginRequest
import com.example.access_tracker.model.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Resultado de una llamada a la API. */
sealed interface ApiResult<out T> {
    data class Ok<T>(val value: T) : ApiResult<T>
    data class Error(val error: ApiError) : ApiResult<Nothing>
}

sealed interface ApiError {
    /** No hay red o el servidor no responde. */
    data object SinConexion : ApiError

    /** 401: credenciales malas (login) o token invalido/expirado/revocado (resto). */
    data object NoAutorizado : ApiError

    /** 429: demasiados intentos. */
    data class Limitado(val reintentarEnSegundos: Long?) : ApiError

    /** Cualquier otro error HTTP. El mensaje viene de la API (en ingles): solo para logs. */
    data class Servidor(val codigo: Int, val mensaje: String?) : ApiError
}

class ApiClient(
    private val baseUrl: String,
    private val sesion: SessionStore,
) {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val _sesionExpirada = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emite cuando una llamada autenticada recibe 401: la UI debe llevar a "sesion expirada". */
    val sesionExpirada: SharedFlow<Unit> = _sesionExpirada.asSharedFlow()

    suspend fun login(usuario: String, password: String): ApiResult<LoginResponse> {
        val body = json.encodeToString(LoginRequest.serializer(), LoginRequest(usuario.trim(), password))
        val r = ejecutar(
            Request.Builder().url("$baseUrl/login").post(body.toRequestBody(jsonMedia)).build(),
            autenticada = false,
        )
        return when (r) {
            is ApiResult.Ok -> ApiResult.Ok(json.decodeFromString(LoginResponse.serializer(), r.value))
            is ApiResult.Error -> r
        }
    }

    /** GET autenticado; decodifica el JSON de la respuesta con [serializer]. */
    suspend fun <T> get(path: String, serializer: KSerializer<T>): ApiResult<T> =
        decodificar(ejecutar(Request.Builder().url("$baseUrl$path").get().build()), serializer)

    /** POST autenticado con cuerpo JSON ya serializado. */
    suspend fun <T> post(path: String, cuerpoJson: String, serializer: KSerializer<T>): ApiResult<T> =
        decodificar(
            ejecutar(Request.Builder().url("$baseUrl$path").post(cuerpoJson.toRequestBody(jsonMedia)).build()),
            serializer,
        )

    /** Serializa [cuerpo] y hace POST. */
    suspend fun <B, T> post(path: String, cuerpo: B, bodySer: KSerializer<B>, serializer: KSerializer<T>) =
        post(path, json.encodeToString(bodySer, cuerpo), serializer)

    private fun <T> decodificar(r: ApiResult<String>, serializer: KSerializer<T>): ApiResult<T> = when (r) {
        is ApiResult.Error -> r
        is ApiResult.Ok -> try {
            ApiResult.Ok(json.decodeFromString(serializer, r.value))
        } catch (e: SerializationException) {
            ApiResult.Error(ApiError.Servidor(200, "Respuesta inesperada del servidor"))
        }
    }

    /** Revoca el token en el servidor. El token local se borra siempre, aunque falle la red. */
    suspend fun logout() {
        val token = sesion.leer()?.token
        sesion.borrar()
        if (token == null) return
        ejecutar(
            Request.Builder().url("$baseUrl/logout").post("".toRequestBody(null))
                .header("Authorization", "Bearer $token").build(),
            autenticada = false,
        )
    }

    /**
     * Ejecuta una peticion y devuelve el cuerpo como texto. Si es [autenticada], agrega el token
     * y ante un 401 borra la sesion y avisa por [sesionExpirada].
     */
    suspend fun ejecutar(request: Request, autenticada: Boolean = true): ApiResult<String> =
        withContext(Dispatchers.IO) {
            val req = if (autenticada) {
                val token = sesion.leer()?.token
                    ?: return@withContext ApiResult.Error(ApiError.NoAutorizado)
                request.newBuilder().header("Authorization", "Bearer $token").build()
            } else {
                request
            }
            try {
                http.newCall(req).execute().use { resp ->
                    val texto = resp.body?.string().orEmpty()
                    when {
                        resp.isSuccessful -> ApiResult.Ok(texto)
                        resp.code == 401 -> {
                            if (autenticada) {
                                sesion.borrar()
                                _sesionExpirada.tryEmit(Unit)
                            }
                            ApiResult.Error(ApiError.NoAutorizado)
                        }
                        resp.code == 429 -> ApiResult.Error(
                            ApiError.Limitado(resp.header("Retry-After")?.toLongOrNull())
                        )
                        else -> ApiResult.Error(ApiError.Servidor(resp.code, mensajeDe(texto)))
                    }
                }
            } catch (e: IOException) {
                ApiResult.Error(ApiError.SinConexion)
            }
        }

    private fun mensajeDe(texto: String): String? = try {
        (json.parseToJsonElement(texto) as? JsonObject)?.get("error")?.jsonPrimitive?.contentOrNull
    } catch (e: Exception) {
        null
    }
}

object ServiceLocator {
    lateinit var sesion: SessionStore
        private set
    lateinit var api: ApiClient
        private set

    /** Para trabajo que debe terminar aunque la pantalla se cierre (p. ej. avisar el logout). */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        sesion = SessionStore(context.applicationContext)
        api = ApiClient(BuildConfig.API_BASE_URL.trimEnd('/'), sesion)
    }
}
