package com.onirica.dreamjournal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onirica.dreamjournal.data.Dream
import com.onirica.dreamjournal.data.DreamRepository
import com.onirica.dreamjournal.interpreter.DreamInterpreter
import kotlinx.coroutines.launch

private const val MIN_LENGTH = 12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(repository: DreamRepository, onOpenHistory: () -> Unit) {
    var dreamText by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<DreamInterpreter.Result?>(null) }
    var savedDream by remember { mutableStateOf<Dream?>(null) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        TopBar(onOpenHistory = onOpenHistory)

        Spacer(Modifier.height(24.dp))

        Text(
            "Racconta il tuo sogno",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "L'interpretazione viene generata interamente sul tuo dispositivo: nessun dato lascia mai il telefono.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = dreamText,
            onValueChange = {
                dreamText = it
                result = null
                savedDream = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            placeholder = { Text("Ero in una casa che non riconoscevo, e...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val analysis = DreamInterpreter.interpret(dreamText)
                result = analysis
                scope.launch {
                    savedDream = repository.add(
                        title = analysis.title,
                        content = dreamText.trim(),
                        interpretation = analysis.interpretation,
                        symbols = analysis.symbols,
                        mood = analysis.mood
                    )
                }
            },
            enabled = dreamText.trim().length >= MIN_LENGTH,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Interpreta il sogno")
        }

        if (dreamText.trim().isNotEmpty() && dreamText.trim().length < MIN_LENGTH) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Racconta qualche dettaglio in più (almeno $MIN_LENGTH caratteri).",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        result?.let { r ->
            Spacer(Modifier.height(28.dp))
            InterpretationCard(r)
            Spacer(Modifier.height(8.dp))
            Text(
                if (savedDream != null) "Salvato nel diario." else "Salvataggio in corso...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun TopBar(onOpenHistory: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.NightsStay,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Onirica",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        TextButton(onClick = onOpenHistory) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Diario")
        }
    }
}

@Composable
private fun InterpretationCard(result: DreamInterpreter.Result) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                result.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(10.dp))
            if (result.symbols.isNotEmpty()) {
                Text(
                    result.symbols.joinToString("  •  "),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
            }
            Text(
                result.interpretation,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
