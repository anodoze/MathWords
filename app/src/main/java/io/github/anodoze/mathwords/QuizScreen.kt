package io.github.anodoze.mathwords

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type

@Composable
fun QuizScreen(viewModel: QuizViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .focusRequester(focusRequester)
        .focusable()
        .onKeyEvent {
            if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
                onBack()
                true
            } else {
                viewModel.handleKeyEvent(it)
            }
        }
    ) {
        when (val s = state) {
            is QuizState.Awaiting -> AwaitingInput(s)
            is QuizState.Wrong -> WrongAnswer(s)
            is QuizState.Loading -> CircularProgressIndicator()
        }
    }
}