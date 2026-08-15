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
import com.onirica.dreamjournal.ui.screens.HistoryScreen
import com.onirica.dreamjournal.ui.screens.HomeScreen
import com.onirica.dreamjournal.ui.theme.OniricaTheme

/**
 * Punto di ingresso dell'app. Onirica è un'applicazione Kotlin nativa
 * standalone: nessun account, nessuna AI remota, nessun riferimento a
 * servizi terzi. Tutto - interpretazione e archivio - vive sul dispositivo.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = DreamRepository(applicationContext)

        setContent {
            OniricaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OnericaApp(repository)
                }
            }
        }
    }
}

private enum class Screen { HOME, HISTORY }

@Composable
private fun OnericaApp(repository: DreamRepository) {
    var screen by remember { mutableStateOf(Screen.HOME) }

    when (screen) {
        Screen.HOME -> HomeScreen(
            repository = repository,
            onOpenHistory = { screen = Screen.HISTORY }
        )
        Screen.HISTORY -> HistoryScreen(
            repository = repository,
            onBack = { screen = Screen.HOME }
        )
    }
}
