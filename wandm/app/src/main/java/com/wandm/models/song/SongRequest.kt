package com.wandm.models.song

import com.wandm.App
import com.wandm.models.RequestCommand
import com.wandm.models.RequestListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

class SongRequest(val title: String, listener: RequestListener<ArrayList<Song>>) : RequestCommand<ArrayList<Song>>(listener) {
    override fun request() {
        doAsync {
            val list = SongUtil.getListMusicModel(title)
            App.instance.runOnUiThread {
                listener.onComplete(list)
            }
        }
    }

}