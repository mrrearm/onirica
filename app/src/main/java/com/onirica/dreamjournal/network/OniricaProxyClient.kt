package com.onirica.dreamjournal.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Client per l'interpretazione AI opzionale, tramite un piccolo server
 * "ponte" (Cloudflare Worker) gestito dallo sviluppatore dell'app.
 *
 * A differenza di una chiamata diretta a un provider AI, qui l'app NON
 * gestisce alcuna chiave API: la chiave resta segreta sul server, e ogni
 * utente dell'app usa lo stesso endpoint condiviso senza doversi
 * registrare da nessuna parte.
 *
 * IMPORTANTE: sostituisci PROXY_BASE_URL con l'URL del tuo worker dopo
 * averlo pubblicato (vedi cartella onirica-proxy/README.md).
 *
 * In caso di qualunque errore (rete assente, worker non raggiungibile,
 * risposta malformata) la funzione restituisce un Result.failure: è
 * compito del chiamante ripiegare sull'interprete locale, così l'app non
 * si blocca mai.
 */
object OniricaProxyClient {

    // TODO: sostituisci con l'URL reale dopo `wrangler deploy`, es.
    // "https://onirica-proxy.mrrearm.workers.dev"
     private const val PROXY_BASE_URL = "https://bitter-unit-9698.mrrearm.workers.dev"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    data class AiInterpretation(
        val title: String,
        val interpretation: String,
        val symbols: List<String> = emptyList(),
        val mood: String = "ambivalente"
    )

    suspend fun interpretDream(dreamText: String): Result<AiInterpretation> =
        withContext(Dispatchers.IO) {
            try {
                val bodyObject = buildJsonObject {
                    put("dream", dreamText)
                }

                val request = Request.Builder()
                    .url(PROXY_BASE_URL)
                    .addHeader("Content-Type", "application/json")
                    .post(bodyObject.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string()
                    if (!response.isSuccessful || bodyString.isNullOrBlank()) {
                        return@withContext Result.failure(
                            IOException("Richiesta al server fallita (HTTP ${response.code})")
                        )
                    }
                    val parsed = json.decodeFromString(AiInterpretation.serializer(), bodyString)
                    Result.success(parsed)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
