package com.example.pixelnothingwidgets.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pixelnothingwidgets.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import com.example.pixelnothingwidgets.data.SettingsDataStore

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsDataStore = SettingsDataStore(this)
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            MaterialTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(settingsDataStore = settingsDataStore, onSave = {
                        // Save completed
                        finish()
                    })
                }
            }
        }
    }
}
