package io.github.anodoze.mathwords

import android.content.Context
import android.net.Uri
import android.os.Environment
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// --- Schema ---

data class ExportedAnswer(
    val responseTimeMs: Long,
    val isCorrect: Boolean,
    val answeredAt: Long
)

data class ExportedCard(
    val operandA: Int,
    val operandB: Int,
    val operation: String,
    val rollingAvgMs: Float,
    val lastAskedAt: Long,
    val reviewIntervalMs: Long,
    val isIntroduced: Boolean,
    val introducedOrder: Int,
    val answers: List<ExportedAnswer>
)

data class ExportData(
    val exportedAt: Long,
    val schemaVersion: Int = 1,
    val cards: List<ExportedCard>
)

// --- Serialization ---

private fun ExportData.toJson(): String {
    val root = JSONObject()
    root.put("exportedAt", exportedAt)
    root.put("schemaVersion", schemaVersion)
    val cardsArray = JSONArray()
    for (card in cards) {
        val cardObj = JSONObject()
        cardObj.put("operandA", card.operandA)
        cardObj.put("operandB", card.operandB)
        cardObj.put("operation", card.operation)
        cardObj.put("rollingAvgMs", card.rollingAvgMs)
        cardObj.put("lastAskedAt", card.lastAskedAt)
        cardObj.put("reviewIntervalMs", card.reviewIntervalMs)
        cardObj.put("isIntroduced", card.isIntroduced)
        cardObj.put("introducedOrder", card.introducedOrder)
        val answersArray = JSONArray()
        for (answer in card.answers) {
            val answerObj = JSONObject()
            answerObj.put("responseTimeMs", answer.responseTimeMs)
            answerObj.put("isCorrect", answer.isCorrect)
            answerObj.put("answeredAt", answer.answeredAt)
            answersArray.put(answerObj)
        }
        cardObj.put("answers", answersArray)
        cardsArray.put(cardObj)
    }
    root.put("cards", cardsArray)
    return root.toString()
}

private fun parseExportData(json: String): ExportData {
    val root = JSONObject(json)
    val cardsArray = root.getJSONArray("cards")
    val cards = (0 until cardsArray.length()).map { i ->
        val c = cardsArray.getJSONObject(i)
        val answersArray = c.getJSONArray("answers")
        val answers = (0 until answersArray.length()).map { j ->
            val a = answersArray.getJSONObject(j)
            ExportedAnswer(
                responseTimeMs = a.getLong("responseTimeMs"),
                isCorrect = a.getBoolean("isCorrect"),
                answeredAt = a.getLong("answeredAt")
            )
        }
        ExportedCard(
            operandA = c.getInt("operandA"),
            operandB = c.getInt("operandB"),
            operation = c.getString("operation"),
            rollingAvgMs = c.getDouble("rollingAvgMs").toFloat(),
            lastAskedAt = c.getLong("lastAskedAt"),
            reviewIntervalMs = c.getLong("reviewIntervalMs"),
            isIntroduced = c.getBoolean("isIntroduced"),
            introducedOrder = c.getInt("introducedOrder"),
            answers = answers
        )
    }
    return ExportData(
        exportedAt = root.getLong("exportedAt"),
        schemaVersion = root.getInt("schemaVersion"),
        cards = cards
    )
}

// --- Export ---

suspend fun exportToDownloads(context: Context, db: MathWordsDatabase): Boolean {
    val cards = mutableListOf<ExportedCard>()

    for (op in Operation.entries) {
        val introduced = db.cardDao().getIntroducedCards(op)
        for (card in introduced) {
            val answers = db.answerDao().getRecentAnswers(card.id, limit = 8)
            cards.add(ExportedCard(
                operandA = card.operandA,
                operandB = card.operandB,
                operation = card.operation.name,
                rollingAvgMs = card.rollingAvgMs,
                lastAskedAt = card.lastAskedAt,
                reviewIntervalMs = card.reviewIntervalMs,
                isIntroduced = card.isIntroduced,
                introducedOrder = card.introducedOrder,
                answers = answers.map { ExportedAnswer(it.responseTimeMs, it.isCorrect, it.answeredAt) }
            ))
        }
    }

    return runCatching {
        val json = ExportData(exportedAt = System.currentTimeMillis(), cards = cards).toJson()
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "mathwords_backup_${System.currentTimeMillis()}.json")
        file.writeText(json)
    }.isSuccess
}

// --- Import ---

suspend fun importFromUri(context: Context, db: MathWordsDatabase, uri: Uri): Boolean {
    val filename = uri.lastPathSegment ?: return false
    if (!filename.endsWith(".json", ignoreCase = true)) return false
    return runCatching {
        val json = context.contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.readText()
            ?: return false

        val data = parseExportData(json)

        for (exportedCard in data.cards) {
            val operation = Operation.valueOf(exportedCard.operation)

            val existing = db.cardDao().getCardByNaturalKey(
                exportedCard.operandA, exportedCard.operandB, operation
            ) ?: continue

            db.cardDao().upsert(existing.copy(
                rollingAvgMs = exportedCard.rollingAvgMs,
                lastAskedAt = exportedCard.lastAskedAt,
                reviewIntervalMs = exportedCard.reviewIntervalMs,
                isIntroduced = exportedCard.isIntroduced,
                introducedOrder = exportedCard.introducedOrder
            ))

            db.answerDao().deleteAnswersForCard(existing.id)
            exportedCard.answers.forEach { a ->
                db.answerDao().insert(Answer(
                    cardId = existing.id,
                    responseTimeMs = a.responseTimeMs,
                    isCorrect = a.isCorrect,
                    answeredAt = a.answeredAt
                ))
            }
        }
    }.isSuccess
}