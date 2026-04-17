package com.example.pixelnothingwidgets.ui.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pixelnothingwidgets.data.SettingsDataStore
import com.example.pixelnothingwidgets.weather.WeatherRepository
import com.example.pixelnothingwidgets.weather.getWeatherConditionText
import com.example.pixelnothingwidgets.weather.mapWeatherCode
import com.example.pixelnothingwidgets.widget.WidgetTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun WeatherDetailScreen(weatherRepository: WeatherRepository) {
    val context = LocalContext.current
    val settingsDataStore = SettingsDataStore(context)
    val cityName by settingsDataStore.cityName.collectAsState(initial = "")
    val timeUtils = WidgetTimeUtils()
    val coroutineScope = rememberCoroutineScope()

    val (weatherData, setWeatherData) = remember {
        mutableStateOf(weatherRepository.getCachedWeatherData())
    }

    // Refresh weather data
    coroutineScope.launch(Dispatchers.IO) {
        val data = weatherRepository.getWeatherData()
        setWeatherData(data)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Weather Detail",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (weatherData != null) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "${weatherData.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val weatherCode = mapWeatherCode(weatherData.weatherCode)
            Text(
                text = getWeatherConditionText(weatherCode),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Humidity: ${(weatherData.humidity * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Last Updated: ${timeUtils.getLastUpdatedTime(weatherData.lastUpdated)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            Text(
                text = "No weather data available",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Please check your settings and network connection",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
