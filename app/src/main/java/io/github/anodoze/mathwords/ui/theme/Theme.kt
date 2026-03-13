package io.github.anodoze.mathwords.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = Highlight,
    onPrimary = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Box,
    onSurface = TextPrimary,
    error = TextError,
    onError = TextPrimary
)

@Composable
fun MathWordsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}