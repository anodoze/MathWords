package io.github.anodoze.mathwords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AwaitingInput(state: QuizState.Awaiting) {
    val isNegative = state.card.correctAnswer() < 0
    val displayInput = when {
        state.input.isEmpty() -> if (isNegative) "-_" else "_"
        else -> if (isNegative) "-${state.input}" else state.input
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${state.card.operandA} ${state.card.operation.symbol()} ${state.card.operandB}",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = displayInput,
            style = MaterialTheme.typography.displayMedium
        )
    }
}