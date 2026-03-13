package io.github.anodoze.mathwords

object DatabaseSeeder {
    suspend fun seed(db: MathWordsDatabase) {
        val cards = mutableListOf<Card>()
        for (op in Operation.entries.filter { it != Operation.DIVIDE }) {
            for (a in 0..99) {
                for (b in 0..99) {
                    cards.add(Card(operandA = a, operandB = b, operation = op))
                }
            }
        }
        db.cardDao().insertAll(cards)
    }
}