package com.onirica.dreamjournal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onirica.dreamjournal.data.SettingsStore

@Composable
fun SettingsScreen(settings: SettingsStore, onBack: () -> Unit) {
    var useAi by remember { mutableStateOf(settings.useAi) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
            }
            Spacer(Modifier.width(4.dp))
            Text(
                "Impostazioni",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Interpretazione AI (opzionale)",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Per impostazione predefinita Onirica interpreta i sogni interamente sul telefono, senza " +
                "connessione a internet. Se attivi questa opzione, l'interpretazione viene invece generata " +
                "da un vero modello linguistico: il testo del sogno viene inviato, solo al momento " +
                "dell'analisi, a un piccolo server gestito dallo sviluppatore dell'app - non serve nessun " +
                "account o chiave personale.",
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Usa interpretazione AI", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "Richiede connessione internet",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = useAi,
                onCheckedChange = {
                    useAi = it
                    settings.useAi = it
                }
            )
        }

        Spacer(Modifier.height(28.dp))
        Text(
            "Anche con l'AI attiva, se la connessione manca o la richiesta fallisce per qualsiasi motivo, " +
                "Onirica genera comunque un'interpretazione in locale: non resterai mai senza risposta.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
