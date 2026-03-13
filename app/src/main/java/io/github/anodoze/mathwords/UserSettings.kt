package io.github.anodoze.mathwords

data class UserSettings(
    val passingThresholdMs: Float = 3000f,
    val maxWeakCards: Int = 10,
    val confirmKey: Char = '#'
)