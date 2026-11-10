package com.example.access_tracker.model

import kotlinx.serialization.Serializable

@Serializable
data class AutorAviso(val id: Long, val name: String, val daycareId: Long? = null)

/** Aviso tal como llega en /notices/inbox (la API no manda fecha de envio). */
@Serializable
data class Aviso(
    val id: Long,
    val subject: String,
    val noticeType: String,       // schedule_change | meeting | reminder | urgent
    val message: String,
    val requiresConfirmation: Boolean = false,
    val author: AutorAviso,
    val read: Boolean = false,
    val readAt: String? = null,
) {
    val urgente get() = noticeType == "urgent"
}

@Serializable
data class AcuseLectura(val noticeId: Long, val read: Boolean, val readAt: String? = null)
