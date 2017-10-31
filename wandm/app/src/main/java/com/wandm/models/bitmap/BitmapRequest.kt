package com.wandm.models.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.wandm.App
import com.wandm.models.RequestCommand
import com.wandm.models.RequestListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class BitmapRequest(private val url: String, listener: RequestListener<Bitmap>) : RequestCommand<Bitmap>(listener) {
    override fun request() {
        doAsync {
            val bitmapResult = getBitmapFromURL()
            App.instance.runOnUiThread {
                listener.onComplete(bitmapResult)
            }
        }
    }

    private fun getBitmapFromURL(): Bitmap? {
        try {
            val url = URL(url)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            return null
        }

    }


}