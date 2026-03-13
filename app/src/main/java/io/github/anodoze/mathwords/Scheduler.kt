package io.github.anodoze.mathwords

import kotlin.math.pow

// Scheduler.kt
class Scheduler(
    private val cardDao: CardDao,
    private val answerDao: AnswerDao,
    private val operation: Operation,
    private val passingThresholdMs: Float,
    private val maxWeakCards: Int,
    private val decayFactor: Float = 0.9f,
    private val wrongAnswerPenaltyMultiplier: Float = 5f
) {
    suspend fun nextCard(): Card? {
        val now = System.currentTimeMillis()

        // 1. Due for review
        val due = cardDao.getCardsDueForReview(operation, now)
        if (due.isNotEmpty()) return due.minByOrNull { it.rollingAvgMs }

        // 2. Weak cards
        val weak = cardDao.getIntroducedCardsByWeakness(operation)
            .filter { it.rollingAvgMs > passingThresholdMs }
        if (weak.size >= maxWeakCards) return weak.first() // already sorted by weakness

        // 3. Introduce a new card if weak pool has room
        val new = cardDao.getUnintroducedCards(operation, 1).firstOrNull()
        if (new != null) return new

        // 4. Fallback
        return weak.firstOrNull()
    }

    suspend fun recordAnswer(card: Card, responseTimeMs: Long, isCorrect: Boolean) {
        val effectiveTime = if (isCorrect) responseTimeMs
        else passingThresholdMs * wrongAnswerPenaltyMultiplier

        answerDao.insert(Answer(cardId = card.id, responseTimeMs = effectiveTime.toLong(), isCorrect = isCorrect))

        val recentAnswers = answerDao.getRecentAnswers(card.id, limit = 20)
        val newAvg = computeWeightedAverage(recentAnswers)
        val newInterval = computeReviewInterval(newAvg)

        cardDao.upsert(card.copy(
            rollingAvgMs = newAvg,
            lastAskedAt = System.currentTimeMillis(),
            reviewIntervalMs = newInterval,
            isIntroduced = true
        ))
    }

    private fun computeWeightedAverage(answers: List<Answer>): Float {
        if (answers.isEmpty()) return 0f
        var weightedSum = 0f
        var weightSum = 0f
        answers.forEachIndexed { i, answer ->
            val weight = decayFactor.pow(i)
            weightedSum += answer.responseTimeMs * weight
            weightSum += weight
        }
        return weightedSum / weightSum
    }

    private fun computeReviewInterval(avgMs: Float): Long {
        val ratio = (passingThresholdMs / avgMs.coerceAtLeast(1f))
        val intervalMs = (ratio * 24 * 60 * 60 * 1000L).toLong() // ratio in days
        return intervalMs.coerceIn(
            4 * 60 * 60 * 1000L,    // floor: 4 hours
            6 * 30 * 24 * 60 * 60 * 1000L // ceiling: 6 months
        )
    }
}