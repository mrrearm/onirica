package com.onirica.dreamjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.onirica.dreamjournal.data.DreamRepository
import com.onirica.dreamjournal.data.SettingsStore
import com.onirica.dreamjournal.ui.screens.HistoryScreen
import com.onirica.dreamjournal.ui.screens.HomeScreen
import com.onirica.dreamjournal.ui.screens.SettingsScreen
import com.onirica.dreamjournal.ui.theme.OniricaTheme

/**
 * Punto di ingresso dell'app. Onirica è un'applicazione Kotlin nativa
 * standalone: nessun account, nessun servizio di terze parti obbligatorio.
 * L'unica funzione che tocca la rete - l'interpretazione AI - è opzionale,
 * disattivata di default, e usa una chiave API personale dell'utente
 * (mai inclusa nel codice del progetto). L'archivio dei sogni vive sempre
 * e solo sul dispositivo.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = DreamRepository(applicationContext)
        val settings = SettingsStore(applicationContext)

        setContent {
            OniricaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OnericaApp(repository, settings)
                }
            }
        }
    }
}

private enum class Screen { HOME, HISTORY, SETTINGS }

@Composable
private fun OnericaApp(repository: DreamRepository, settings: SettingsStore) {
    var screen by remember { mutableStateOf(Screen.HOME) }

    when (screen) {
        Screen.HOME -> HomeScreen(
            repository = repository,
            onOpenHistory = { screen = Screen.HISTORY },
            onOpenSettings = { screen = Screen.SETTINGS }
        )
        Screen.HISTORY -> HistoryScreen(
            repository = repository,
            onBack = { screen = Screen.HOME }
        )
        Screen.SETTINGS -> SettingsScreen(
            settings = settings,
            onBack = { screen = Screen.HOME }
        )
    }
}
