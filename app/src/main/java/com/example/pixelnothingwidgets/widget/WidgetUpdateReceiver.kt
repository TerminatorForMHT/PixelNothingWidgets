package com.example.pixelnothingwidgets.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pixelnothingwidgets.widget.pixel.PixelClockWidgetProvider
import com.example.pixelnothingwidgets.widget.pixel.PixelWeatherWidgetProvider
import com.example.pixelnothingwidgets.widget.pixel.PixelClockWeatherWidgetProvider
import com.example.pixelnothingwidgets.widget.nothing.NothingClockWidgetProvider
import com.example.pixelnothingwidgets.widget.nothing.NothingWeatherWidgetProvider

class WidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.pixelnothingwidgets.ACTION_UPDATE_WIDGETS") {
            // Update all widgets
            PixelClockWidgetProvider.updateAllWidgets(context)
            PixelWeatherWidgetProvider.updateAllWidgets(context)
            PixelClockWeatherWidgetProvider.updateAllWidgets(context)
            NothingClockWidgetProvider.updateAllWidgets(context)
            NothingWeatherWidgetProvider.updateAllWidgets(context)
        }
    }
}
