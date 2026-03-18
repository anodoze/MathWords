package io.github.anodoze.mathwords

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

class QuizViewModel(
    private val scheduler: Scheduler,
    private val settings: UserSettings,
) : ViewModel() {
    private val _state = MutableStateFlow<QuizState>(QuizState.Loading)
    val state: StateFlow<QuizState> = _state
    val decimalPrecision: Int get() = settings.decimalPrecision

    init {
        viewModelScope.launch { nextCard() }
    }

    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        val digit = when (event.key) {
            Key.Zero -> "0"
            Key.One -> "1"
            Key.Two -> "2"
            Key.Three -> "3"
            Key.Four -> "4"
            Key.Five -> "5"
            Key.Six -> "6"
            Key.Seven -> "7"
            Key.Eight -> "8"
            Key.Nine -> "9"
            else -> null
        }

        val isConfirm = event.key == Key(if (settings.confirmKey == '#') 18 else 17)
        val isBackspace = event.key == Key(if (settings.confirmKey == '#') 17 else 18)

        when (val s = _state.value) {
            is QuizState.Awaiting -> when {
                digit != null -> _state.value = s.copy(input = s.input + digit)
                isBackspace -> _state.value = s.copy(input = s.input.dropLast(1))
                isConfirm -> submit(s)
                else -> return false
            }
            is QuizState.Wrong -> if (isConfirm) {
                viewModelScope.launch { nextCard() }
            }
            else -> return false
        }
        return true
    }

    private var cardShownAt: Long = 0L
    private fun submit(state: QuizState.Awaiting) {
        val raw = state.input.toLongOrNull() ?: return
        val factor = 10f.pow(settings.decimalPrecision)
        val answer = raw / factor * if (state.card.correctAnswer() < 0) -1 else 1
        val correct = state.card.correctAnswer()
        val isCorrect = (answer * factor).roundToInt() == (correct * factor).roundToInt()
        val responseTimeMs = System.currentTimeMillis() - cardShownAt
        viewModelScope.launch {
            scheduler.recordAnswer(state.card, responseTimeMs, isCorrect)
            if (isCorrect) nextCard()
            else _state.value = QuizState.Wrong(state.card, correct)
        }
    }

    private suspend fun nextCard() {
        val card = scheduler.nextCard() ?: return
        cardShownAt = System.currentTimeMillis()
        _state.value = QuizState.Awaiting(card, input = "")
    }
}