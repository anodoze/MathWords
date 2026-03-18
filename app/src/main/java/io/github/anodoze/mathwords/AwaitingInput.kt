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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.pow

@Composable
fun AwaitingInput(state: QuizState.Awaiting, decimalPrecision: Int) {
    if (state.card.operation == Operation.DIVIDE)
        DecimalInput(state, decimalPrecision)
    else
        IntegerInput(state)
}

@Composable
fun IntegerInput(state: QuizState.Awaiting) {
    val isNegative = state.card.correctAnswer() < 0
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    val cursor = if (cursorVisible) "|" else " "
    val displayInput = buildAnnotatedString {
        append(when {
            state.input.isEmpty() -> if (isNegative) "-$cursor" else cursor
            else -> if (isNegative) "-${state.input}$cursor" else "${state.input}$cursor"
        })
    }

    InputBox(state, displayInput)
}

@Composable
fun DecimalInput(state: QuizState.Awaiting, decimalPrecision: Int) {
    val isNegative = state.card.correctAnswer() < 0
    var cursorVisible by remember { mutableStateOf(true) }

    val ghostColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val primaryColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    val cursor = if (cursorVisible) "|" else " "
    val ghost = "0.${"0".repeat(decimalPrecision)}"
    val displayInput = when {
        state.input.isEmpty() -> buildAnnotatedString {
            if (isNegative) append("-")
            withStyle(SpanStyle(color = ghostColor)) { append(ghost) }
            withStyle(SpanStyle(color = primaryColor)) { append(cursor) }
        }
        else -> buildAnnotatedString {
            val formatted = formatDecimalDisplay(state.input, decimalPrecision)
            if (isNegative) append("-")
            if (state.input.length > decimalPrecision) {
                append(formatted)
            } else {
                val splitIndex = formatted.length - state.input.length
                withStyle(SpanStyle(color = ghostColor)) { append(formatted.take(splitIndex)) }
                append(formatted.drop(splitIndex))
            }
            withStyle(SpanStyle(color = primaryColor)) { append(cursor) }
        }
    }

    InputBox(state, displayInput)
}

fun formatDecimalDisplay(input: String, decimalPrecision: Int): String {
    val padded = input.padStart(decimalPrecision, '0')
    val intPart = padded.dropLast(decimalPrecision).ifEmpty { "0" }
    val decPart = padded.takeLast(decimalPrecision)
    return "$intPart.$decPart"
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