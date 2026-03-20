package io.github.anodoze.mathwords

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AwaitingInput(state: QuizState.Awaiting, sigFigs: Int) {
    val isDecimal = state.card.operation == Operation.DIVIDE
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    val cursor = if (cursorVisible) "|" else " "
    val correct = state.card.correctAnswer()
    val primaryColor = MaterialTheme.colorScheme.onSurface

    val displayInput = buildAnnotatedString {
        if (correct < 0) append("-")
        append(state.input)
        withStyle(SpanStyle(color = primaryColor)) { append(cursor) }
    }

    InputBox(state, displayInput)
}

fun ghostPlaceholder(correct: Float, sigFigs: Int): String {
    if (correct == 0f) return "0"
    val magnitude = kotlin.math.floor(kotlin.math.log10(kotlin.math.abs(correct))).toInt()
    val decimalPlaces = (sigFigs - 1 - magnitude).coerceAtLeast(0)
    return if (decimalPlaces == 0) "0"
    else "0.${"0".repeat(decimalPlaces)}"
}

@Composable
fun InputBox(state: QuizState.Awaiting, displayInput: AnnotatedString) {
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
            Text(text = displayInput, style = MaterialTheme.typography.displayMedium)
        }
    }
}