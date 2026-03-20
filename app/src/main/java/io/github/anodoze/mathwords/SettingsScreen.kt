package io.github.anodoze.mathwords

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

enum class SettingsField { THRESHOLD, MAX_WEAK, SIG_FIGS, CONFIRM_KEY, SAVE }

@Composable
fun SettingsScreen(
    settings: UserSettings,
    onSave: (UserSettings) -> Unit,
    onBack: () -> Unit
) {
    var selectedField by remember { mutableStateOf(SettingsField.THRESHOLD) }
    var thresholdInput by remember { mutableStateOf(
        (settings.passingThresholdMs / 10f).toInt().toString()
    )}
    var maxWeakInput by remember { mutableStateOf(settings.maxWeakCards.toString()) }
    var sigFigsInput by remember { mutableStateOf(settings.sigFigs.toString()) }
    var confirmKey by remember { mutableStateOf(settings.confirmKey) }

    val fields = SettingsField.entries
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    LaunchedEffect(selectedField) { listState.animateScrollToItem(selectedField.ordinal) }

    fun handleDigit(digit: String) {
        when (selectedField) {
            SettingsField.THRESHOLD -> thresholdInput += digit
            SettingsField.MAX_WEAK -> maxWeakInput += digit
            SettingsField.SIG_FIGS -> sigFigsInput += digit
            else -> Unit
        }
    }

    fun handleBackspace() {
        when (selectedField) {
            SettingsField.THRESHOLD -> thresholdInput = thresholdInput.dropLast(1)
            SettingsField.MAX_WEAK -> maxWeakInput = maxWeakInput.dropLast(1)
            SettingsField.SIG_FIGS -> sigFigsInput = sigFigsInput.dropLast(1)
            else -> Unit
        }
    }

    fun handleSave() {
        val thresholdMs = (thresholdInput.toFloatOrNull() ?: (settings.passingThresholdMs / 10f)) * 10f
        onSave(UserSettings(
            passingThresholdMs = thresholdMs,
            maxWeakCards = maxWeakInput.toIntOrNull() ?: settings.maxWeakCards,
            confirmKey = confirmKey,
            sigFigs = sigFigsInput.toIntOrNull() ?: settings.sigFigs
        ))
        onBack()
    }

    fun handleConfirm() {
        when (selectedField) {
            SettingsField.CONFIRM_KEY -> confirmKey = if (confirmKey == '#') '*' else '#'
            SettingsField.SAVE -> handleSave()
            else -> {
                val next = (fields.indexOf(selectedField) + 1) % fields.size
                selectedField = fields[next]
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.key == Key.Back && event.type == KeyEventType.KeyUp) {
                    onBack()
                    return@onKeyEvent true
                }
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                val digit = when (event.key) {
                    Key.Zero -> "0"; Key.One -> "1"; Key.Two -> "2"
                    Key.Three -> "3"; Key.Four -> "4"; Key.Five -> "5"
                    Key.Six -> "6"; Key.Seven -> "7"; Key.Eight -> "8"
                    Key.Nine -> "9"; else -> null
                }
                val isConfirm = event.key == Key(if (settings.confirmKey == '#') 18 else 17)
                        || event.key == Key.DirectionCenter
                val isBackspace = event.key == Key(if (settings.confirmKey == '#') 17 else 18)
                when {
                    event.key == Key.DirectionDown -> {
                        val next = (fields.indexOf(selectedField) + 1) % fields.size
                        selectedField = fields[next]
                    }
                    event.key == Key.DirectionUp -> {
                        val prev = (fields.indexOf(selectedField) - 1 + fields.size) % fields.size
                        selectedField = fields[prev]
                    }
                    digit != null -> handleDigit(digit)
                    isBackspace -> handleBackspace()
                    isConfirm -> handleConfirm()
                    else -> return@onKeyEvent false
                }
                true
            }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Settings", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                SettingsInputField(
                    label = "Passing threshold (seconds)",
                    input = thresholdInput,
                    isActive = selectedField == SettingsField.THRESHOLD,
                    isDecimal = true,
                    decimalPrecision = 2
                )
            }
            item {
                SettingsInputField(
                    label = "Max weak cards",
                    input = maxWeakInput,
                    isActive = selectedField == SettingsField.MAX_WEAK,
                    isDecimal = false
                )
            }
            item {
                SettingsInputField(
                    label = "Significant figures",
                    input = sigFigsInput,
                    isActive = selectedField == SettingsField.SIG_FIGS,
                    isDecimal = false
                )
            }
            item {
                ConfirmKeyToggle(
                    selected = confirmKey,
                    isActive = selectedField == SettingsField.CONFIRM_KEY
                )
            }
            item {
                AppButton(
                    onClick = { handleSave() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedField == SettingsField.SAVE)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Text("Save")
                }
            }
            item {
                Text(
                    text = "↑↓ navigate   ${settings.confirmKey} confirm   back cancel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SettingsInputField(
    label: String,
    input: String,
    isActive: Boolean,
    isDecimal: Boolean,
    decimalPrecision: Int = 2
) {
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(500)
            cursorVisible = !cursorVisible
        }
    }
    val cursor = if (isActive && cursorVisible) "|" else " "

    val displayText = if (isDecimal) {
        val padded = input.padStart(decimalPrecision, '0')
        val intPart = padded.dropLast(decimalPrecision).ifEmpty { "0" }
        val decPart = padded.takeLast(decimalPrecision)
        "$intPart.$decPart$cursor"
    } else {
        if (input.isEmpty()) cursor else "$input$cursor"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(displayText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ConfirmKeyToggle(selected: Char, isActive: Boolean) {
    val options = listOf('#', '*')
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Confirm key", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { key ->
                Text(
                    text = key.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (key == selected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}