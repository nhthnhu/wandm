package com.wandm.models.menu

import android.content.res.Resources
import com.wandm.App
import com.wandm.R
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder
import net.steamcrafted.materialiconlib.MaterialIconUtils

class ListMenus private constructor() {
    private val mList = ArrayList<Menu>()

    companion object {
        val instance by lazy { ListMenus() }
    }

    init {
        val songs = Menu(MaterialDrawableBuilder.IconValue.MUSIC_NOTE, App.instance.resources.getString(R.string.songs),
                false, R.color.color_white)

        val favorite = Menu(MaterialDrawableBuilder.IconValue.HEART, App.instance.resources.getString(R.string.favorites),
                false, R.color.color_red)

        val artists = Menu(MaterialDrawableBuilder.IconValue.MUSIC_NOTE, App.instance.resources.getString(R.string.artists),
                false, R.color.color_white)

        val setting = Menu(MaterialDrawableBuilder.IconValue.SETTINGS, "Setting",
                false, R.color.color_white)

        mList.add(favorite)
        mList.add(setting)
    }

    operator fun get(position: Int): Menu {
        return mList[position]
    }

    fun size() = mList.size
}