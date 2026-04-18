package com.example.pixelnothingwidgets.widget.nothing

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
import com.example.pixelnothingwidgets.widget.WidgetTimeUtils
import com.example.pixelnothingwidgets.system.DynamicColorHelper

class NothingClockWidgetProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.nothing_clock_widget)
            val timeUtils = WidgetTimeUtils()
            val dynamicColorHelper = DynamicColorHelper(context)

            // Apply dynamic colors
            val primaryColor = dynamicColorHelper.getDynamicColor()
            val textColor = dynamicColorHelper.getTextColorForBackground(primaryColor)

            // Set widget text colors
            views.setInt(R.id.time_text, "setTextColor", textColor)
            views.setInt(R.id.date_text, "setTextColor", textColor)

            // Set time and date
            views.setTextViewText(R.id.time_text, timeUtils.getCurrentTime())
            views.setTextViewText(R.id.date_text, timeUtils.getCurrentDate())

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

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NothingClockWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
