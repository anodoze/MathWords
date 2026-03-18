package io.github.anodoze.mathwords

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE isIntroduced = 1 AND operation = :op ORDER BY rollingAvgMs DESC")
    suspend fun getIntroducedCardsByWeakness(op: Operation): List<Card>

    @Query("SELECT * FROM cards WHERE isIntroduced = 0 AND operation = :op ORDER BY introducedOrder ASC LIMIT :limit")
    suspend fun getUnintroducedCards(op: Operation, limit: Int): List<Card>

    @Query("SELECT * FROM cards WHERE isIntroduced = 1 AND operation = :op AND lastAskedAt + reviewIntervalMs < :now")
    suspend fun getCardsDueForReview(op: Operation, now: Long): List<Card>

    @Upsert
    suspend fun upsert(card: Card)

    @Insert
    suspend fun insertAll(cards: List<Card>)
}