package com.example.pixelnothingwidgets.widget.pixel

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.RemoteViews
import com.example.pixelnothingwidgets.R
import com.example.pixelnothingwidgets.system.DynamicColorHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PixelCalendarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.pixel_calendar_widget)
            val dynamicColorHelper = DynamicColorHelper(context)

            // Apply dynamic colors
            val primaryColor = dynamicColorHelper.getDynamicColor()
            val textColor = dynamicColorHelper.getTextColorForBackground(primaryColor)

            // Set widget text colors
            views.setInt(R.id.date_text, "setTextColor", textColor)
            views.setInt(R.id.day_text, "setTextColor", textColor)
            views.setInt(R.id.event_count_text, "setTextColor", textColor)

            // Set date and day
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            val dayFormat = SimpleDateFormat("d", Locale.getDefault())
            
            views.setTextViewText(R.id.date_text, dateFormat.format(calendar.time))
            views.setTextViewText(R.id.day_text, dayFormat.format(calendar.time))

            // Set event count (mock data for now)
            views.setTextViewText(R.id.event_count_text, "2 events today")

            // Set click intent (open calendar app)
            val calendarIntent = Intent(Intent.ACTION_VIEW)
            calendarIntent.data = CalendarContract.CONTENT_URI
            calendarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val calendarPendingIntent = PendingIntent.getActivity(
                context,
                0,
                calendarIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.date_text, calendarPendingIntent)
            views.setOnClickPendingIntent(R.id.day_text, calendarPendingIntent)
            views.setOnClickPendingIntent(R.id.event_count_text, calendarPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PixelCalendarWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}