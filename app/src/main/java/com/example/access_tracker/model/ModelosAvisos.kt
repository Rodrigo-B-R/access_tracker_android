package com.example.access_tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class AutorAviso(val id: String, val name: String, val daycareId: String? = null)

/** Aviso tal como llega en /notices/inbox. La API los manda del mas nuevo al mas viejo. */
@Serializable
data class Aviso(
    val id: String,
    val subject: String,
    val noticeType: String,       // schedule_change | meeting | reminder | urgent
    val message: String,
    val requiresConfirmation: Boolean = false,
    val author: AutorAviso,
    val read: Boolean = false,
    val readAt: String? = null,
    /** Fecha de envio (ISO-8601 UTC). */
    val createdAt: String? = null,
) {
    val urgente get() = noticeType == "urgent"
}

@Serializable
data class AcuseLectura(val noticeId: String, val read: Boolean, val readAt: String? = null)
