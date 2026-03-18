package io.github.anodoze.mathwords

import android.util.Log

object DatabaseSeeder {
    suspend fun seed(db: MathWordsDatabase) {
        val cards = mutableListOf<Card>()
        for (op in Operation.entries) {
            for (a in 0..99) {
                for (b in 0..99) {
                    if (op == Operation.DIVIDE && b == 0) continue
                    cards.add(Card(
                        operandA = a,
                        operandB = b,
                        operation = op,
                        introducedOrder = maxOf(a, b) * 100 + minOf(a, b)
                    ))
                }
            }
        }
        val divideCount = cards.count { it.operation == Operation.DIVIDE }
        Log.d("Seeder", "DIVIDE cards: $divideCount")
        db.cardDao().insertAll(cards)
    }
}