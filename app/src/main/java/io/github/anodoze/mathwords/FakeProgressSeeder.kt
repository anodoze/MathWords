package io.github.anodoze.mathwords

import kotlin.random.Random

object FakeProgressSeeder {
    suspend fun seed(db: MathWordsDatabase) {
        val ceiling = 6L * 30 * 24 * 3600 * 1000
        for (op in Operation.entries) {
            val cards = db.cardDao().getUnintroducedCards(op, 10000) // grab all
            cards.forEach { card ->
                val max = maxOf(card.operandA, card.operandB)
                val introduced = when (op) {
                    Operation.MULTIPLY -> max <= 12 && Random.nextFloat() > 0.3f
                    Operation.DIVIDE -> max <= 20 && Random.nextFloat() > 0.5f
                    else -> max <= 50 && Random.nextFloat() > (max / 80f)
                }
                if (!introduced) return@forEach
                db.cardDao().upsert(card.copy(
                    isIntroduced = true,
                    reviewIntervalMs = (Random.nextFloat() * ceiling).toLong()
                ))
            }
        }
    }
}