package com.example.pixelnothingwidgets.system

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.provider.AlarmClock

class SystemAppLauncher(private val context: Context) {

    fun openClockApp() {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general clock app
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setClassName("com.android.deskclock", "com.android.deskclock.DeskClock")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (ex: Exception) {
                // If all else fails, do nothing
            }
        }
    }

    fun openCalendarApp() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = CalendarContract.CONTENT_URI
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // If calendar app not available, do nothing
        }
    }
}
