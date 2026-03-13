package io.github.anodoze.mathwords

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.content.edit

class MathWordsApplication : Application() {
    val database by lazy { MathWordsDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("mathwords", MODE_PRIVATE)
        if (!prefs.getBoolean("seeded", false)) {
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseSeeder.seed(database)
                prefs.edit { putBoolean("seeded", true) }
            }
        }
    }
}