package com.example.pixelnothingwidgets.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import com.example.pixelnothingwidgets.weather.WeatherData
import com.example.pixelnothingwidgets.weather.WeatherRepository
import kotlinx.coroutines.runBlocking

class WidgetRenderer(private val context: Context) {

    private val weatherRepository = WeatherRepository(context)

    fun getWeatherData(): WeatherData? {
        return runBlocking {
            weatherRepository.getWeatherData()
        }
    }

    fun updateWidget(appWidgetId: Int, remoteViews: RemoteViews, widgetClass: Class<*>) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    fun updateAllWidgets(appWidgetIds: IntArray, remoteViews: RemoteViews, widgetClass: Class<*>) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }
}
