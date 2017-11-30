package com.wandm.loaders

import com.wandm.App
import com.wandm.models.RequestCommand
import com.wandm.models.RequestListener
import com.wandm.models.Song
import com.wandm.utils.OnlineSongUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

class OnlineSongLoader(val title: String, listener: RequestListener<ArrayList<Song>>) : RequestCommand<ArrayList<Song>>(listener) {
    override fun request() {
        doAsync {
            val list = OnlineSongUtil.getListMusicModel(title)
            App.instance.runOnUiThread {
                listener.onComplete(list)
            }
        }
    }

}