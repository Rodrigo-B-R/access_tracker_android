package com.example.access_tracker.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executor
import kotlin.coroutines.resume

/** Lectura de GPS con LocationManager (sin Google Play Services). */
object Gps {
    private fun concedido(context: Context, permiso: String) =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    /** Android 12+: el usuario puede dar solo ubicacion aproximada; con eso basta para leer la posicion. */
    fun tienePermiso(context: Context) =
        concedido(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
            concedido(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    /** Posicion actual o null si no hay permiso, el GPS esta apagado o no responde en [timeoutMs]. */
    @SuppressLint("MissingPermission")
    suspend fun actual(context: Context, timeoutMs: Long = 12_000): Location? {
        if (!tienePermiso(context)) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // El proveedor GPS exige permiso preciso; con solo aproximada se usa el de red.
        val precisa = concedido(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val proveedor = when {
            precisa && lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return null
        }
        val ejecutor = Executor { it.run() }
        return withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { cont ->
                val cancelar = CancellationSignal()
                cont.invokeOnCancellation { cancelar.cancel() }
                lm.getCurrentLocation(proveedor, cancelar, ejecutor) { loc -> cont.resume(loc) }
            }
        }
    }
}
