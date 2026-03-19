package io.github.anodoze.mathwords

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Screen { HOME, QUIZ, SETTINGS }

@Composable
fun MathWordsApp(database: MathWordsDatabase, settings: UserSettings, onSaveSettings: (UserSettings) -> Unit) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedOperation by remember { mutableStateOf(Operation.ADD) }

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            onOperationSelected = {
                selectedOperation = it
                currentScreen = Screen.QUIZ
            },
            onSettingsSelected = { currentScreen = Screen.SETTINGS }
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
                settings = settings
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
        Screen.SETTINGS -> SettingsScreen(
            settings = settings,
            onSave = onSaveSettings,
            onBack = { currentScreen = Screen.HOME }
        )
    }
}