package com.wandm.utils

import android.app.Activity
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import com.ms_square.etsyblur.BlurConfig
import com.wandm.R
import com.wandm.SmartAsyncPolicyHolder


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
        if (PreferencesUtils.getLightTheme()) {
            activity.setTheme(R.style.LightTheme)
        } else
            activity.setTheme(R.style.DarkTheme)
    }


    val DEBUG = java.lang.Boolean.parseBoolean("true")

    /**
     * @return BlurConfig to support BlurringView of EtsyBlur
     */
    fun getBlurViewConfig(): BlurConfig {
        val isLightTheme = PreferencesUtils.getLightTheme()
        if (isLightTheme)
            return BlurConfig.Builder()
                    .radius(22)
                    .overlayColor(Color.argb(80, 225, 225, 225))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .build()
        else
            return BlurConfig.Builder()
                    .radius(22)
                    .overlayColor(Color.argb(55, 0, 0, 0))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .build()
    }
}
