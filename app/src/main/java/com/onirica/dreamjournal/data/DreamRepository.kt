package com.onirica.dreamjournal.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

/**
 * Database "integrato" dell'app: un file JSON salvato nella storage privata
 * dell'applicazione (/data/data/com.onirica.dreamjournal/files/dreams.json).
 *
 * Non richiede alcun server, connessione di rete o dipendenza esterna:
 * è un vero database locale, semplicemente serializzato in JSON invece
 * che in un formato binario come SQLite. Per il volume di dati di un
 * diario dei sogni personale, leggere/scrivere l'intero file ad ogni
 * operazione è più che sufficiente e molto più semplice da ispezionare,
 * fare il backup o esportare rispetto a un DB relazionale.
 */
class DreamRepository(context: Context) {

    private val file: File = File(context.filesDir, "dreams.json")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun getAll(): List<Dream> = withContext(Dispatchers.IO) {
        readJournal().dreams.sortedByDescending { it.createdAt }
    }

    suspend fun getById(id: String): Dream? = withContext(Dispatchers.IO) {
        readJournal().dreams.firstOrNull { it.id == id }
    }

    suspend fun add(title: String, content: String, interpretation: String, symbols: List<String>, mood: String): Dream =
        withContext(Dispatchers.IO) {
            val journal = readJournal()
            val dream = Dream(
                id = UUID.randomUUID().toString(),
                title = title,
                content = content,
                interpretation = interpretation,
                symbols = symbols,
                mood = mood,
                createdAt = System.currentTimeMillis()
            )
            writeJournal(journal.copy(dreams = journal.dreams + dream))
            dream
        }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        val journal = readJournal()
        writeJournal(journal.copy(dreams = journal.dreams.filterNot { it.id == id }))
    }

    private fun readJournal(): DreamJournal {
        if (!file.exists()) return DreamJournal()
        return try {
            json.decodeFromString(DreamJournal.serializer(), file.readText())
        } catch (e: Exception) {
            // File corrotto o formato inatteso: non far crashare l'app,
            // si riparte con un diario vuoto piuttosto che perdere l'utente.
            DreamJournal()
        }
    }

    private fun writeJournal(journal: DreamJournal) {
        file.writeText(json.encodeToString(DreamJournal.serializer(), journal))
    }
}
