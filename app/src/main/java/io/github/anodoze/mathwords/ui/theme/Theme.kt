package io.github.anodoze.mathwords.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    primary = Highlight,
    onPrimary = TextPrimary,
    background = Background,
    surface = Background,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextAccent,
    error = TextError,
    onError = TextPrimary,
    surfaceVariant = Box,
    onErrorContainer = TextError
)

@Composable
fun MathWordsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}