package com.example.access_tracker.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formato de fechas y horas. La API trabaja en UTC; las estancias, en hora de Monterrey. */
object Fechas {
    val ZONA: ZoneId = ZoneId.of("America/Monterrey")
    private val ES = Locale.forLanguageTag("es-MX")

    fun hoy(): LocalDate = LocalDate.now(ZONA)

    /** "2026-11-04T13:05:00.000Z" -> "7:05" (hora local de la estancia). */
    fun horaLocal(iso: String): String =
        DateTimeFormatter.ofPattern("H:mm").format(Instant.parse(iso).atZone(ZONA))

    /** "07:00" -> "7:00". */
    fun horaCorta(hhmm: String): String = hhmm.removePrefix("0")

    /** "Lunes 9 de noviembre". */
    fun fechaLarga(d: LocalDate = hoy()): String =
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES).format(d)
            .replaceFirstChar { it.uppercase(ES) }
}
