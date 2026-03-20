package io.github.anodoze.mathwords

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AnswerDao {
    @Insert
    suspend fun insert(answer: Answer)

    @Query("SELECT * FROM answers WHERE cardId = :cardId ORDER BY answeredAt DESC LIMIT :limit")
    suspend fun getRecentAnswers(cardId: Long, limit: Int): List<Answer>

    @Query("DELETE FROM answers WHERE cardId = :cardId AND id NOT IN (SELECT id FROM answers WHERE cardId = :cardId ORDER BY answeredAt DESC LIMIT :limit)")
    suspend fun pruneOldAnswers(cardId: Long, limit: Int)
}