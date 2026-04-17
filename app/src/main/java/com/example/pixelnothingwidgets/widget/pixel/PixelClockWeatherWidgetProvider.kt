package com.example.pixelnothingwidgets.widget.pixel

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.RemoteViews
import com.example.pixelnothingwidgets.R
import com.example.pixelnothingwidgets.ui.weather.WeatherDetailActivity
import com.example.pixelnothingwidgets.widget.WidgetRenderer
import com.example.pixelnothingwidgets.widget.WidgetTimeUtils
import com.example.pixelnothingwidgets.data.SettingsDataStore
import kotlinx.coroutines.runBlocking

class PixelClockWeatherWidgetProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.pixel_clock_weather_widget)
            val widgetRenderer = WidgetRenderer(context)
            val timeUtils = WidgetTimeUtils()
            val settingsDataStore = SettingsDataStore(context)

            // Set time and date
            views.setTextViewText(R.id.time_text, timeUtils.getCurrentTime())
            views.setTextViewText(R.id.date_text, timeUtils.getCurrentDate())

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

                views.setTextViewText(R.id.city_text, cityName)
                views.setTextViewText(R.id.temperature_text, "${weatherData.temperature.toInt()}°")
                views.setTextViewText(R.id.condition_text, weatherData.weatherCondition)
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
                    token.isEmpty() -> views.setTextViewText(R.id.error_text, "Token required")
                    latitude.isEmpty() || longitude.isEmpty() -> views.setTextViewText(R.id.error_text, "Location required")
                    else -> views.setTextViewText(R.id.error_text, "Sync failed")
                }
            }

            // Set click intent for time (open clock app)
            val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val clockPendingIntent = PendingIntent.getActivity(
                context,
                0,
                clockIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.time_text, clockPendingIntent)

            // Set click intent for date (open calendar app)
            val calendarIntent = Intent(Intent.ACTION_VIEW)
            calendarIntent.data = CalendarContract.CONTENT_URI
            calendarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val calendarPendingIntent = PendingIntent.getActivity(
                context,
                1,
                calendarIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.date_text, calendarPendingIntent)

            // Set click intent for weather section (open weather detail activity)
            val weatherDetailIntent = Intent(context, WeatherDetailActivity::class.java)
            val weatherDetailPendingIntent = PendingIntent.getActivity(
                context,
                2,
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
            val componentName = ComponentName(context, PixelClockWeatherWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
