package com.example.pixelnothingwidgets.system

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class DynamicColorHelper(private val context: Context) {

    @SuppressLint("NewApi")
    fun getDynamicColor(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val wallpaperManager = context.getSystemService(Context.WALLPAPER_SERVICE) as android.app.WallpaperManager
                val wallpaperColors = wallpaperManager.getWallpaperColors(android.app.WallpaperManager.FLAG_SYSTEM)
                if (wallpaperColors != null) {
                    val primaryColor = wallpaperColors.primaryColor
                    return primaryColor.toArgb()
                }
            } catch (e: Exception) {
                // Fallback to default color if wallpaper colors not available
            }
        }
        // Fallback color for older Android versions
        return ContextCompat.getColor(context, com.example.pixelnothingwidgets.R.color.purple_500)
    }

    fun getTextColorForBackground(backgroundColor: Int): Int {
        // Calculate contrast ratio to determine text color
        val luminance = getLuminance(backgroundColor)
        return if (luminance > 0.5) {
            Color.BLACK // Light background, use black text
        } else {
            Color.WHITE // Dark background, use white text
        }
    }

    private fun getLuminance(color: Int): Double {
        val r = Color.red(color) / 255.0
        val g = Color.green(color) / 255.0
        val b = Color.blue(color) / 255.0

        val rLinear = if (r <= 0.03928) r / 12.92 else Math.pow((r + 0.055) / 1.055, 2.4)
        val gLinear = if (g <= 0.03928) g / 12.92 else Math.pow((g + 0.055) / 1.055, 2.4)
        val bLinear = if (b <= 0.03928) b / 12.92 else Math.pow((b + 0.055) / 1.055, 2.4)

        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }
}
