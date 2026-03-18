package io.github.anodoze.mathwords

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.pow

@Composable
fun AwaitingInput(state: QuizState.Awaiting, decimalPrecision: Int) {
    val isNegative = state.card.correctAnswer() < 0
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    val cursor = if (cursorVisible) "|" else " "
    val displayInput = when {
        state.input.isEmpty() -> if (isNegative) "-$cursor" else cursor
        else -> {
            val factor = 10f.pow(decimalPrecision)
            val formatted = (state.input.toLongOrNull() ?: 0L) / factor
            "%.${decimalPrecision}f".format(formatted)
                .let { if (isNegative) "-$it$cursor" else "$it$cursor" }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = state.card.operation.title(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
        )
        Text(
            text = "${state.card.operandA} ${state.card.operation.symbol()} ${state.card.operandB}",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .fillMaxWidth(0.6f)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayInput,
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}