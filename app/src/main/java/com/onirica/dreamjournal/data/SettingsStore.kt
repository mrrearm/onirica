package com.onirica.dreamjournal.data

import android.content.Context

/**
 * Impostazioni locali dell'app, salvate solo sul dispositivo tramite
 * SharedPreferences.
 *
 * `useAi` controlla l'unica funzione opzionale che richiede una
 * connessione di rete: l'interpretazione generata da un vero modello
 * linguistico, tramite il server "ponte" gestito dallo sviluppatore
 * dell'app (nessuna chiave personale richiesta all'utente). Di default
 * `useAi` è false: l'app resta interamente offline finché non sei tu ad
 * attivarla esplicitamente.
 */
class SettingsStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("onirica_settings", Context.MODE_PRIVATE)

    var useAi: Boolean
        get() = prefs.getBoolean(KEY_USE_AI, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_AI, value).apply()

    companion object {
        private const val KEY_USE_AI = "use_ai"
    }
}
