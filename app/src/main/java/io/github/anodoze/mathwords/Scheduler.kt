package io.github.anodoze.mathwords

import android.util.Log
import kotlin.math.pow
import kotlin.random.Random

class Scheduler(
    private val cardDao: CardDao,
    private val answerDao: AnswerDao,
    private val operation: Operation,
    private val passingThresholdMs: Float,
    private val maxWeakCards: Int,
    private val decayFactor: Float = 0.9f,
    private val answerWindowSize: Int = 8
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
        val effectiveTime = responseTimeMs
            .coerceAtMost((passingThresholdMs * 8f).toLong())

        answerDao.insert(Answer(cardId = card.id, responseTimeMs = effectiveTime, isCorrect = isCorrect))
        answerDao.pruneOldAnswers(card.id, limit = answerWindowSize)

        val recentAnswers = answerDao.getRecentAnswers(card.id, limit = 8)
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
        val effectiveAvg = avgMs.coerceAtLeast(500f)
        val ratio = passingThresholdMs / effectiveAvg
        val intervalMs = (ratio.toDouble().pow(2.5) * 24 * 60 * 60 * 1000L).toLong()
        return intervalMs.coerceIn(
            4 * 60 * 60 * 1000L, // floor: 4h
            6L * 30 * 24 * 60 * 60 * 1000 //ceil: 6mo
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