package io.github.anodoze.mathwords

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    cardsByOperation: Map<Operation, List<Card>>,
    onOperationSelected: (Operation) -> Unit,
    onSettingsSelected: () -> Unit
) {
    val ops = Operation.entries
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ops.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { op ->
                    if (op != null) {
                        AppButton(
                            onClick = { onOperationSelected(op) },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = op.title(),
                                    maxLines = 1,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    autoSize = TextAutoSize.StepBased(maxFontSize = 16.sp),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                )
                                ProgressMatrix(
                                    cards = cardsByOperation[op] ?: emptyList(),
                                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f).wrapContentHeight())
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        AppButton(
            onClick = onSettingsSelected,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }
    }
}

fun Operation.title() = when (this) {
    Operation.ADD -> "Addition"
    Operation.SUBTRACT -> "Subtraction"
    Operation.MULTIPLY -> "Multiplication"
    Operation.DIVIDE -> "Division"
}

fun Operation.symbol() = when (this) {
    Operation.ADD -> "+"
    Operation.SUBTRACT -> "−"
    Operation.MULTIPLY -> "×"
    Operation.DIVIDE -> "÷"
}