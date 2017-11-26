package com.wandm.utils

import android.app.Activity
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import com.wandm.R


object Utils {

    fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
    }

    fun getAlbumArtForFile(filePath: String): String {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(filePath)

        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    }

    fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * Convert drawable to bitmap
     *
     * @param drawable is drawable to convert
     * @return bitmap was converted
     */
    fun convertDrawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)

        return bitmap
    }

    fun applyLightTheme(activity: Activity) {
        when (getTheme()) {
            PreferencesUtils.LIGHT_THEME_SMALL -> activity.setTheme(R.style.LightThemeSmall)
            PreferencesUtils.LIGHT_THEME_MEDIUM -> activity.setTheme(R.style.LightThemeMedium)
            PreferencesUtils.LIGHT_THEME_LARGE -> activity.setTheme(R.style.LightThemeLarge)
            PreferencesUtils.DARK_THEME_SMALL -> activity.setTheme(R.style.DarkThemeSmall)
            PreferencesUtils.DARK_THEME_MEDIUM -> activity.setTheme(R.style.DarkThemeMedium)
            PreferencesUtils.DARK_THEME_LARGE -> activity.setTheme(R.style.DarkThemeLarge)

        }
    }

    fun getTheme(): String {
        val textSize = getTextSize()
        val isLightTheme = PreferencesUtils.getLightTheme()

        if (isLightTheme) {
            when (textSize) {
                14 -> return PreferencesUtils.LIGHT_THEME_SMALL
                18 -> return PreferencesUtils.LIGHT_THEME_MEDIUM
                22 -> return PreferencesUtils.LIGHT_THEME_LARGE
            }
        } else {
            when (textSize) {
                14 -> return PreferencesUtils.DARK_THEME_SMALL
                18 -> return PreferencesUtils.DARK_THEME_MEDIUM
                22 -> return PreferencesUtils.DARK_THEME_LARGE
            }
        }

        return PreferencesUtils.DARK_THEME_MEDIUM
    }

    fun getTextSize(): Int {
        if (PreferencesUtils.getSmallText())
            return 14

        if (PreferencesUtils.getMediumText())
            return 18

        return 22
    }
}
