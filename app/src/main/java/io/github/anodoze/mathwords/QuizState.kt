package io.github.anodoze.mathwords

sealed class QuizState {
    object Loading : QuizState()
    data class Awaiting(val card: Card, val input: String) : QuizState()
    data class Wrong(val card: Card, val correctAnswer: Int) : QuizState()
}