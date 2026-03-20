package io.github.anodoze.mathwords

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WrongAnswer(state: QuizState.Wrong, sigFigs: Int) {
    val decimalPlaces = if (state.correctAnswer == 0f) 0
    else {
        val magnitude = kotlin.math.floor(kotlin.math.log10(kotlin.math.abs(state.correctAnswer))).toInt()
        (sigFigs - 1 - magnitude).coerceAtLeast(0)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = state.card.operation.title(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${state.card.operandA} ${state.card.operation.symbol()} ${state.card.operandB}",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "%.${decimalPlaces}f".format(state.correctAnswer),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Text(
            text = "press # to continue",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
        )
    }
}