package com.aaa.gptenniscoach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Applies the Tennis Coach theme with Material Design 3 to composable content.
 */
@Composable
fun TennisCoachTheme(content: @Composable () -> Unit) {
    // V0: light scheme by default; adjust later.
    val colors = lightColorScheme()
    MaterialTheme(colorScheme = colors, content = content)
}

