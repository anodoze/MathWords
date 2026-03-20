package io.github.anodoze.mathwords

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.content.edit

class MathWordsApplication : Application() {
    val database by lazy { MathWordsDatabase.getInstance(this) }

    var userSettings: UserSettings by mutableStateOf(UserSettings())
        private set

    fun saveSettings(settings: UserSettings) {
        userSettings = settings
        UserSettings.save(this, settings)
    }

    override fun onCreate() {
        super.onCreate()
        userSettings = UserSettings.load(this)
        val prefs = getSharedPreferences("mathwords", MODE_PRIVATE)
        if (!prefs.getBoolean("seeded", false)) {
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseSeeder.seed(database)
                if (BuildConfig.DEBUG) FakeProgressSeeder.seed(database)
                prefs.edit { putBoolean("seeded", true) }
            }
        }
    }
}