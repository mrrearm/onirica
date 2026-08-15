package com.onirica.dreamjournal.data

import kotlinx.serialization.Serializable

/**
 * Rappresenta un sogno registrato e interpretato interamente in locale.
 * Nessun campo fa riferimento a servizi esterni, AI o account remoti:
 * ogni sogno vive solo nel file JSON salvato sul dispositivo dell'utente.
 */
@Serializable
data class Dream(
    val id: String,
    val title: String,
    val content: String,
    val interpretation: String,
    val symbols: List<String> = emptyList(),
    val mood: String,
    val createdAt: Long
)

/**
 * Contenitore radice del file dreams.json: una semplice lista di sogni.
 * Usare un oggetto radice (anziché un array nudo) rende il formato
 * più facile da estendere in futuro (es. versione dello schema).
 */
@Serializable
data class DreamJournal(
    val schemaVersion: Int = 1,
    val dreams: List<Dream> = emptyList()
)
