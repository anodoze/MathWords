package io.github.anodoze.mathwords

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Composable
fun ProgressMatrix(cards: List<Card>, modifier: Modifier = Modifier) {
    val weak = MaterialTheme.colorScheme.error
    val strong = Color(0xFFA6E3A1) // or pull from your theme

    val progressMap = remember(cards) {
        val ceilingMs = 6L * 30 * 24 * 3600 * 1000
        val halfCeiling = ceilingMs * 0.5f
        cards.associate { (it.operandA to it.operandB) to
                (it.reviewIntervalMs / halfCeiling).coerceIn(0f, 1f) }
    }

    Canvas(modifier = modifier) {
        val blockW = size.width / 100f
        val blockH = size.height / 100f
        for (a in 0..99) {
            for (b in 0..99) {
                val p = progressMap[a to b] ?: continue
                drawRect(
                    color = lerp(weak, strong, p),
                    topLeft = Offset(b * blockW, a * blockH),
                    size = Size(blockW, blockH)
                )
            }
        }
    }
}