package com.wandm.models.song

import android.graphics.Bitmap
import android.util.Log
import com.wandm.models.RequestListener
import com.wandm.models.bitmap.BitmapRequest
import org.json.JSONArray
import java.net.URL

object SongUtil {
    private val TAG = "SongUtil"

    fun getListMusicModel(name: String): ArrayList<Song>? {
        val url = "http://j.ginggong.com/jOut.ashx?" +
                "k=" + name.replace(" ", "%20") + "&" +
                "h=mp3.zing.vn&" +
                "code=0d014f53-b002-4126-8c4a-a3f8270fb794"


        val jsonStr = URL(url).readText()
        Log.d(TAG, url)

        return parseJSON(jsonStr)
    }

    private fun parseJSON(jsonStr: String): ArrayList<Song>? {
        val jsonArray = JSONArray(jsonStr)
        val list = ArrayList<Song>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val title = jsonObject.getString("Title").replace("\u0026", "&")
            val avatarUrl = jsonObject.getString("Avatar").replace("\u0026", "&")
            val urlDownload = jsonObject.getString("UrlJunDownload").replace("\u0026", "&")

            var bitmap: Bitmap? = null
            val bitmapListener = object : RequestListener<Bitmap> {
                override fun onComplete(data: Bitmap?) {
                    bitmap = data
                    Log.d(TAG, data.toString())
                }
            }
            BitmapRequest(avatarUrl, bitmapListener).execute()

            val song = Song(title, urlDownload, bitmap)

            list.add(song)
        }

        if (list.size == 0)
            return null

        return list
    }

}