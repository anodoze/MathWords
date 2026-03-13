package io.github.anodoze.mathwords

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "answers",
    foreignKeys = [ForeignKey(
        entity = Card::class,
        parentColumns = ["id"],
        childColumns = ["cardId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("cardId")]
)
data class Answer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val responseTimeMs: Long,
    val isCorrect: Boolean,
    val answeredAt: Long = System.currentTimeMillis()
)
