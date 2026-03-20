package io.github.anodoze.mathwords

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Screen { HOME, QUIZ, SETTINGS }

@Composable
fun MathWordsApp(database: MathWordsDatabase, settings: UserSettings, onSaveSettings: (UserSettings) -> Unit) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedOperation by remember { mutableStateOf(Operation.ADD) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch(Dispatchers.IO) {
                val success = exportToDownloads(context, database)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        if (success) "Exported to Downloads" else "Export failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                context,
                "Storage permission denied — enable it in Settings > Apps > MathWords > Permissions",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val success = importFromUri(context, database, it)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        if (success) "Import successful" else "Import failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    when (currentScreen) {
        Screen.HOME -> {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(database))
            val cardsByOp by homeViewModel.cardsByOperation.collectAsState()
            HomeScreen(
                cardsByOperation = cardsByOp,
                onOperationSelected = {
                    selectedOperation = it
                    currentScreen = Screen.QUIZ
                },
                onSettingsSelected = { currentScreen = Screen.SETTINGS }
            )
        }
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
            onBack = { currentScreen = Screen.HOME },
            onExport = {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        scope.launch(Dispatchers.IO) {
                            val success = exportToDownloads(context, database)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    if (success) "Exported to Downloads" else "Export failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else -> permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            },
            onImport = {
                importLauncher.launch("*/*")
            }
        )
    }
}