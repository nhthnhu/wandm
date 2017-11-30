package com.wandm.utils

import android.util.Log
import com.wandm.models.Song
import org.json.JSONArray
import java.net.URL

object OnlineSongUtil {
    private val TAG = "OnlineSongUtil"

    fun getListMusicModel(name: String): ArrayList<Song>? {
        val url = "http://j.ginggong.com/jOut.ashx?" +
                "k=" + name.replace(" ", "%20") + "&" +
                "h=keeng.vn&" +
                "code=0d014f53-b002-4126-8c4a-a3f8270fb794"

//        http://j.ginggong.com/jOut.ashx?k=attention&h=mp3.zing.vn&code=0d014f53-b002-4126-8c4a-a3f8270fb794

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

            val song = Song(title, urlDownload, avatarUrl)
            list.add(song)
        }

        if (list.size == 0)
            return null

        return list
    }

}