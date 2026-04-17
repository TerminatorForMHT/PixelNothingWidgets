package com.example.pixelnothingwidgets.widget.nothing

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.pixelnothingwidgets.R
import com.example.pixelnothingwidgets.ui.weather.WeatherDetailActivity
import com.example.pixelnothingwidgets.weather.getNothingStyleWeatherText
import com.example.pixelnothingwidgets.weather.mapWeatherCode
import com.example.pixelnothingwidgets.widget.WidgetRenderer
import com.example.pixelnothingwidgets.data.SettingsDataStore
import kotlinx.coroutines.runBlocking

class NothingWeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Start periodic weather sync when widget is first added
        com.example.pixelnothingwidgets.work.WeatherSyncWorker.enqueueWork(context)
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.nothing_weather_widget)
            val widgetRenderer = WidgetRenderer(context)
            val settingsDataStore = SettingsDataStore(context)

            // Get weather data
            val weatherData = widgetRenderer.getWeatherData()
            val cityName = runBlocking {
                settingsDataStore.cityName.first()
            }

            if (weatherData != null && cityName.isNotEmpty()) {
                // Show weather data
                views.setViewVisibility(R.id.city_text, android.view.View.VISIBLE)
                views.setViewVisibility(R.id.temperature_text, android.view.View.VISIBLE)
                views.setViewVisibility(R.id.condition_text, android.view.View.VISIBLE)
                views.setViewVisibility(R.id.error_text, android.view.View.GONE)

                views.setTextViewText(R.id.city_text, cityName.uppercase())
                views.setTextViewText(R.id.temperature_text, "${weatherData.temperature.toInt()}°")
                val weatherCode = mapWeatherCode(weatherData.weatherCode)
                views.setTextViewText(R.id.condition_text, getNothingStyleWeatherText(weatherCode))
            } else {
                // Show error message
                views.setViewVisibility(R.id.city_text, android.view.View.GONE)
                views.setViewVisibility(R.id.temperature_text, android.view.View.GONE)
                views.setViewVisibility(R.id.condition_text, android.view.View.GONE)
                views.setViewVisibility(R.id.error_text, android.view.View.VISIBLE)

                val token = runBlocking {
                    settingsDataStore.caiyunToken.first()
                }
                val latitude = runBlocking {
                    settingsDataStore.latitude.first()
                }
                val longitude = runBlocking {
                    settingsDataStore.longitude.first()
                }

                when {
                    token.isEmpty() -> views.setTextViewText(R.id.error_text, "TOKEN REQUIRED")
                    latitude.isEmpty() || longitude.isEmpty() -> views.setTextViewText(R.id.error_text, "LOCATION REQUIRED")
                    else -> views.setTextViewText(R.id.error_text, "SYNC FAILED")
                }
            }

            // Set click intent (open weather detail activity)
            val weatherDetailIntent = Intent(context, WeatherDetailActivity::class.java)
            val weatherDetailPendingIntent = PendingIntent.getActivity(
                context,
                0,
                weatherDetailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.city_text, weatherDetailPendingIntent)
            views.setOnClickPendingIntent(R.id.temperature_text, weatherDetailPendingIntent)
            views.setOnClickPendingIntent(R.id.condition_text, weatherDetailPendingIntent)
            views.setOnClickPendingIntent(R.id.error_text, weatherDetailPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NothingWeatherWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
