package io.github.anodoze.mathwords

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Screen { HOME, QUIZ }

@Composable
fun MathWordsApp(database: MathWordsDatabase, settings: UserSettings) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedOperation by remember { mutableStateOf(Operation.ADD) }

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            onOperationSelected = {
                selectedOperation = it
                currentScreen = Screen.QUIZ
            }
        )
        Screen.QUIZ -> {
            val factory = QuizViewModelFactory(
                scheduler = Scheduler(
                    cardDao = database.cardDao(),
                    answerDao = database.answerDao(),
                    operation = selectedOperation,
                    passingThresholdMs = settings.passingThresholdMs,
                    maxWeakCards = settings.maxWeakCards
                ),
                confirmKey = settings.confirmKey
            )
            val viewModel: QuizViewModel = viewModel(
                key = selectedOperation.name,
                factory = factory
            )
            QuizScreen(
                viewModel = viewModel,
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}