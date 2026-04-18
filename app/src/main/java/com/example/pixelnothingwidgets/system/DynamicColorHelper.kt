package com.example.pixelnothingwidgets.system

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

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

    @SuppressLint("NewApi")
    fun getSecondaryColor(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val wallpaperManager = context.getSystemService(Context.WALLPAPER_SERVICE) as android.app.WallpaperManager
                val wallpaperColors = wallpaperManager.getWallpaperColors(android.app.WallpaperManager.FLAG_SYSTEM)
                if (wallpaperColors != null) {
                    val secondaryColor = wallpaperColors.secondaryColor
                    if (secondaryColor != null) {
                        return secondaryColor.toArgb()
                    }
                }
            } catch (e: Exception) {
                // Fallback to default color if wallpaper colors not available
            }
        }
        // Fallback color
        return ContextCompat.getColor(context, com.example.pixelnothingwidgets.R.color.teal_200)
    }

    @SuppressLint("NewApi")
    fun getTertiaryColor(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val wallpaperManager = context.getSystemService(Context.WALLPAPER_SERVICE) as android.app.WallpaperManager
                val wallpaperColors = wallpaperManager.getWallpaperColors(android.app.WallpaperManager.FLAG_SYSTEM)
                if (wallpaperColors != null) {
                    val tertiaryColor = wallpaperColors.tertiaryColor
                    if (tertiaryColor != null) {
                        return tertiaryColor.toArgb()
                    }
                }
            } catch (e: Exception) {
                // Fallback to default color if wallpaper colors not available
            }
        }
        // Fallback color
        return ContextCompat.getColor(context, com.example.pixelnothingwidgets.R.color.amber_200)
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

    fun getSurfaceColor(backgroundColor: Int): Int {
        // Create a surface color based on the dynamic color
        return ColorUtils.setAlphaComponent(backgroundColor, 200) // 78% opacity
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
