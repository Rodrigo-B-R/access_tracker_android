package com.example.access_tracker.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.access_tracker.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Archivo de DataStore con la sesion: files/datastore/sesion.preferences_pb (excluido de los respaldos).
 * Se declara una sola vez: dos instancias del mismo archivo hacen que la app truene.
 * La primera vez copia lo que habia en SharedPreferences ("session.xml") y lo borra. Ese token viejo
 * no esta cifrado, asi que no se puede descifrar: al actualizar la app se pide iniciar sesion una vez.
 */
private val Context.sesionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sesion",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, "session")) },
)

/** Sesion guardada en el telefono. */
data class Session(
    val token: String,
    val role: String,
    val userId: Long,
    val name: String,
    val daycareId: Long?,
    val expiresAtMillis: Long,
) {
    fun vigente(ahora: Long = System.currentTimeMillis()) = ahora < expiresAtMillis
}

/** Claves dentro del archivo. Son los mismos nombres que usaba SharedPreferences: no cambiarlos. */
private object ClavesSesion {
    val TOKEN = stringPreferencesKey("token")
    val ROLE = stringPreferencesKey("role")
    val USER_ID = longPreferencesKey("userId")
    val NAME = stringPreferencesKey("name")
    val DAYCARE_ID = longPreferencesKey("daycareId") // -1 = sin estancia
    val EXPIRES_AT = longPreferencesKey("expiresAt")
}

/** Cifra el token con AES-256-GCM. La llave vive en el Android Keystore y no se puede extraer. */
private object CifradoToken {
    private const val ALIAS = "sonrisas_token_key"
    private const val TRANSFORMACION = "AES/GCM/NoPadding"
    private const val IV_BYTES = 12
    private const val TAG_BITS = 128

    private fun llave(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gen.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return gen.generateKey()
    }

    /** Devuelve IV + texto cifrado en Base64. */
    fun cifrar(texto: String): String {
        val c = Cipher.getInstance(TRANSFORMACION)
        c.init(Cipher.ENCRYPT_MODE, llave())
        val cifrado = c.doFinal(texto.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(c.iv + cifrado, Base64.NO_WRAP)
    }

    /** Si no se puede descifrar (llave borrada, token viejo o alterado) devuelve null y la app pide login. */
    fun descifrar(guardado: String): String? = try {
        val b = Base64.decode(guardado, Base64.NO_WRAP)
        val c = Cipher.getInstance(TRANSFORMACION)
        c.init(Cipher.DECRYPT_MODE, llave(), GCMParameterSpec(TAG_BITS, b, 0, IV_BYTES))
        String(c.doFinal(b, IV_BYTES, b.size - IV_BYTES), Charsets.UTF_8)
    } catch (e: Exception) {
        null
    }
}

class SessionStore(private val store: DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.sesionDataStore)

    /** La sesion actual; emite de nuevo cada vez que se guarda o se borra. null = no hay sesion. */
    val sesion: Flow<Session?> = store.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e } // archivo ilegible: sin sesion
        .map { p ->
            val token = p[ClavesSesion.TOKEN]?.let(CifradoToken::descifrar) ?: return@map null
            val role = p[ClavesSesion.ROLE] ?: return@map null
            Session(
                token = token,
                role = role,
                userId = p[ClavesSesion.USER_ID] ?: 0,
                name = p[ClavesSesion.NAME] ?: "",
                daycareId = (p[ClavesSesion.DAYCARE_ID] ?: -1L).takeIf { it >= 0 },
                expiresAtMillis = p[ClavesSesion.EXPIRES_AT] ?: 0,
            )
        }

    /** Lee la sesion una vez (sin quedarse escuchando). */
    suspend fun leer(): Session? = sesion.first()

    /** Guarda todo junto: o se guarda completo, o nada. */
    suspend fun guardar(r: LoginResponse) {
        store.edit { p ->
            p[ClavesSesion.TOKEN] = CifradoToken.cifrar(r.token)
            p[ClavesSesion.ROLE] = r.role
            p[ClavesSesion.USER_ID] = r.user.id
            p[ClavesSesion.NAME] = r.user.name
            p[ClavesSesion.DAYCARE_ID] = r.user.daycareId ?: -1L
            p[ClavesSesion.EXPIRES_AT] = System.currentTimeMillis() + r.expiresIn * 1000
        }
    }

    suspend fun borrar() {
        store.edit { it.clear() }
    }
}