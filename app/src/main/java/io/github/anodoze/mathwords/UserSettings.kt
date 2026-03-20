package io.github.anodoze.mathwords

import android.content.Context
import androidx.core.content.edit

data class UserSettings(
    val passingThresholdMs: Float = 3000f,
    val maxWeakCards: Int = 10,
    val confirmKey: Char = '#',
    val sigFigs: Int = 3
) {
    companion object {
        private const val PREFS_NAME = "mathwords"
        private const val KEY_PASSING_THRESHOLD = "passingThresholdMs"
        private const val KEY_MAX_WEAK_CARDS = "maxWeakCards"
        private const val KEY_CONFIRM_KEY = "confirmKey"
        private const val KEY_SIG_FIGS = "sigFigs"

        fun load(context: Context): UserSettings {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return UserSettings(
                passingThresholdMs = prefs.getFloat(KEY_PASSING_THRESHOLD, 3000f),
                maxWeakCards = prefs.getInt(KEY_MAX_WEAK_CARDS, 10),
                confirmKey = prefs.getString(KEY_CONFIRM_KEY, "#")!!.first(),
                sigFigs = prefs.getInt(KEY_SIG_FIGS, 3)
            )
        }

        fun save(context: Context, settings: UserSettings) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putFloat(KEY_PASSING_THRESHOLD, settings.passingThresholdMs)
                putInt(KEY_MAX_WEAK_CARDS, settings.maxWeakCards)
                putString(KEY_CONFIRM_KEY, settings.confirmKey.toString())
                putInt(KEY_SIG_FIGS, settings.sigFigs)
            }
        }
    }
}

val UserSettings.confirmKeyCode: Int get() = if (confirmKey == '#') 18 else 17
val UserSettings.backspaceKeyCode: Int get() = if (confirmKey == '#') 17 else 18