package com.example.pixelnothingwidgets.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pixelnothingwidgets.data.SettingsDataStore
import com.example.pixelnothingwidgets.work.WeatherSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsDataStore: SettingsDataStore, onSave: () -> Unit) {
    val context = LocalContext.current
    val token by settingsDataStore.caiyunToken.collectAsState(initial = "")
    val latitude by settingsDataStore.latitude.collectAsState(initial = "")
    val longitude by settingsDataStore.longitude.collectAsState(initial = "")
    val cityName by settingsDataStore.cityName.collectAsState(initial = "")

    var tokenInput by remember { mutableStateOf(token) }
    var latitudeInput by remember { mutableStateOf(latitude) }
    var longitudeInput by remember { mutableStateOf(longitude) }
    var cityNameInput by remember { mutableStateOf(cityName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Weather Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            label = { Text("Caiyun API Token") },
            placeholder = { Text("Enter your API token") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = latitudeInput,
            onValueChange = { latitudeInput = it },
            label = { Text("Latitude") },
            placeholder = { Text("e.g. 39.9042") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = longitudeInput,
            onValueChange = { longitudeInput = it },
            label = { Text("Longitude") },
            placeholder = { Text("e.g. 116.4074") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = cityNameInput,
            onValueChange = { cityNameInput = it },
            label = { Text("City Display Name") },
            placeholder = { Text("e.g. Beijing") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            singleLine = true
        )

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    settingsDataStore.saveSettings(
                        tokenInput,
                        latitudeInput,
                        longitudeInput,
                        cityNameInput
                    )
                    // Trigger immediate weather sync
                    WeatherSyncWorker.enqueueWork(context)
                }
                onSave()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
