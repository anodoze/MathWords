package io.github.anodoze.mathwords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// HomeScreen.kt
@Composable
fun HomeScreen(onOperationSelected: (Operation) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Operation.entries.forEach { op ->
            Button(
                onClick = { onOperationSelected(op) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(op.symbol())
            }
        }
    }
}

fun Operation.symbol() = when (this) {
    Operation.ADD -> "Addition  +"
    Operation.SUBTRACT -> "Subtraction  −"
    Operation.MULTIPLY -> "Multiplication  ×"
    Operation.DIVIDE -> "Division  ÷"
}