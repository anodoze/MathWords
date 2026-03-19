package io.github.anodoze.mathwords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.anodoze.mathwords.ui.theme.MathWordsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = (application as MathWordsApplication).database
        enableEdgeToEdge()
        setContent {
            MathWordsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MathWordsApp(
                            database = database,
                            settings = (application as MathWordsApplication).userSettings,
                            onSaveSettings = { (application as MathWordsApplication).saveSettings(it) }
                        )
                    }
                }
            }
        }
    }
}
