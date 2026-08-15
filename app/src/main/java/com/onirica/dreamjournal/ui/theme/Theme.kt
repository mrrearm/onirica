package com.onirica.dreamjournal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Indigo = Color(0xFF6C5CE7)
private val DeepNight = Color(0xFF0B0B1A)
private val Surface = Color(0xFF15152B)
private val Gold = Color(0xFFE0C089)
private val TextPrimary = Color(0xFFF3F1FF)
private val TextSecondary = Color(0xFFB2AEDB)

private val OniricaColorScheme = darkColorScheme(
    primary = Indigo,
    secondary = Gold,
    background = DeepNight,
    surface = Surface,
    onPrimary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSecondary = DeepNight,
    surfaceVariant = Surface,
    onSurfaceVariant = TextSecondary
)

@Composable
fun OniricaTheme(content: @Composable () -> Unit) {
    // L'app usa sempre la palette scura "cosmica": è parte dell'identità
    // visiva di Onirica, coerente a prescindere dal tema di sistema.
    val darkTheme = isSystemInDarkTheme() || true
    MaterialTheme(
        colorScheme = OniricaColorScheme,
        content = content
    )
}
