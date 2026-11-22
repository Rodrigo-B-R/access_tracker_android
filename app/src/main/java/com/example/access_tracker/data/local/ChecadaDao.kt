package com.example.access_tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChecadaDao {

    /** Si ya existe una checada con el mismo (uuid, tipo) no se duplica: se ignora. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(checada: ChecadaLocal): Long

    /** Las que faltan por enviar de esa persona, en orden de llegada. */
    @Query("SELECT * FROM cola_checadas WHERE usuarioId = :usuarioId AND estado = 'PENDIENTE' ORDER BY id")
    suspend fun pendientes(usuarioId: String): List<ChecadaLocal>

    @Query("SELECT EXISTS(SELECT 1 FROM cola_checadas WHERE estado = 'PENDIENTE')")
    suspend fun hayPendientes(): Boolean

    @Query(
        "UPDATE cola_checadas SET estado = 'ENVIADA', procesadoEn = :ahora " +
            "WHERE uuid = :uuid AND tipo = :tipo AND estado = 'PENDIENTE'"
    )
    suspend fun marcarEnviada(uuid: String, tipo: String, ahora: Long)

    @Query(
        "UPDATE cola_checadas SET estado = 'RECHAZADA', motivoRechazo = :motivo, procesadoEn = :ahora " +
            "WHERE uuid = :uuid AND tipo = :tipo AND estado = 'PENDIENTE'"
    )
    suspend fun marcarRechazada(uuid: String, tipo: String, motivo: String, ahora: Long)

    @Query("UPDATE cola_checadas SET intentos = intentos + 1 WHERE uuid = :uuid AND tipo = :tipo")
    suspend fun sumarIntento(uuid: String, tipo: String)

    /** Rechazos de esa persona que todavia no se le han mostrado. */
    @Query(
        "SELECT * FROM cola_checadas WHERE usuarioId = :usuarioId AND estado = 'RECHAZADA' " +
            "AND avisoMostrado = 0 ORDER BY id"
    )
    suspend fun rechazosSinAviso(usuarioId: String): List<ChecadaLocal>

    @Query(
        "UPDATE cola_checadas SET avisoMostrado = 1 " +
            "WHERE usuarioId = :usuarioId AND estado = 'RECHAZADA'"
    )
    suspend fun marcarAvisosMostrados(usuarioId: String)

    /**
     * Borra las que ya terminaron (enviadas, o rechazadas cuyo aviso ya se vio) antes de [antesDe].
     * Las PENDIENTE nunca se borran.
     */
    @Query(
        "DELETE FROM cola_checadas WHERE procesadoEn < :antesDe " +
            "AND (estado = 'ENVIADA' OR (estado = 'RECHAZADA' AND avisoMostrado = 1))"
    )
    suspend fun borrarTerminadasAntesDe(antesDe: Long): Int
}
