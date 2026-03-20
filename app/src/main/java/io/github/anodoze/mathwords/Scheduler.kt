package io.github.anodoze.mathwords

import android.util.Log
import kotlin.math.pow
import kotlin.random.Random

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

        val due = cardDao.getCardsDueForReview(operation, now)
        Log.d("Scheduler", "[$operation] due: ${due.size}")
        if (due.isNotEmpty()) return due.minByOrNull { it.rollingAvgMs }

        val weak = cardDao.getIntroducedCardsByWeakness(operation)
            .filter { it.rollingAvgMs > passingThresholdMs }
        Log.d("Scheduler", "[$operation] weak: ${weak.size}")
        if (weak.size >= maxWeakCards) return weak.first()

        val new = cardDao.getUnintroducedCards(operation, 1).firstOrNull()
        Log.d("Scheduler", "[$operation] new: $new")
        if (new != null) return new

        val fallback = weak.firstOrNull()
        Log.d("Scheduler", "[$operation] fallback: $fallback")
        return fallback
    }

    suspend fun recordAnswer(card: Card, responseTimeMs: Long, isCorrect: Boolean) {
        val effectiveTime = if (isCorrect) responseTimeMs
        else passingThresholdMs * wrongAnswerPenaltyMultiplier

        answerDao.insert(Answer(cardId = card.id, responseTimeMs = effectiveTime.toLong(), isCorrect = isCorrect))

        val recentAnswers = answerDao.getRecentAnswers(card.id, limit = 20)
        val newAvg = computeWeightedAverage(recentAnswers)
        val newInterval = fuzzInterval(computeReviewInterval(newAvg))

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
            6L * 30 * 24 * 60 * 60 * 1000 // ceiling: 6 months
        )
    }

    private fun fuzzInterval(intervalMs: Long): Long {
        val maxFuzz = minOf(intervalMs * 0.2, 24 * 60 * 60 * 1000.0).toLong()
        val fuzz = ((Random.nextFloat() * 2 - 1) * maxFuzz).toLong()
        return (intervalMs + fuzz).coerceIn(
            4 * 60 * 60 * 1000L,
            6L * 30 * 24 * 3600 * 1000
        )
    }
}