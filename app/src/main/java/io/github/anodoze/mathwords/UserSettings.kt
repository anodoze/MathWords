package io.github.anodoze.mathwords

import android.content.Context
import androidx.core.content.edit

data class UserSettings(
    val passingThresholdMs: Float = 3000f,
    val maxWeakCards: Int = 10,
    val confirmKey: Char = '#',
    val decimalPrecision: Int = 2
) {
    companion object {
        private const val PREFS_NAME = "mathwords"
        private const val KEY_PASSING_THRESHOLD = "passingThresholdMs"
        private const val KEY_MAX_WEAK_CARDS = "maxWeakCards"
        private const val KEY_CONFIRM_KEY = "confirmKey"
        private const val KEY_DECIMAL_PRECISION = "decimalPrecision"

        fun load(context: Context): UserSettings {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return UserSettings(
                passingThresholdMs = prefs.getFloat(KEY_PASSING_THRESHOLD, 3000f),
                maxWeakCards = prefs.getInt(KEY_MAX_WEAK_CARDS, 10),
                confirmKey = prefs.getString(KEY_CONFIRM_KEY, "#")!!.first(),
                decimalPrecision = prefs.getInt(KEY_DECIMAL_PRECISION, 2)
            )
        }

        fun save(context: Context, settings: UserSettings) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putFloat(KEY_PASSING_THRESHOLD, settings.passingThresholdMs)
                putInt(KEY_MAX_WEAK_CARDS, settings.maxWeakCards)
                putString(KEY_CONFIRM_KEY, settings.confirmKey.toString())
                putInt(KEY_DECIMAL_PRECISION, settings.decimalPrecision)
            }
        }
    }
}