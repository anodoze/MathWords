package io.github.anodoze.mathwords

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val operandA: Int,
    val operandB: Int,
    val operation: Operation,
    val rollingAvgMs: Float = 0f,
    val lastAskedAt: Long = 0L,       // epoch ms
    val reviewIntervalMs: Long = 0L,
    val isIntroduced: Boolean = false,
    val introducedOrder: Int = 0
)

fun Card.correctAnswer(): Float = when (operation) {
    Operation.ADD -> (operandA + operandB).toFloat()
    Operation.SUBTRACT -> (operandA - operandB).toFloat()
    Operation.MULTIPLY -> (operandA * operandB).toFloat()
    Operation.DIVIDE -> operandA.toFloat() / operandB.toFloat()
}

