package com.wandm.models.menu

import com.wandm.R
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

class ListMenus private constructor() {
    private val mList = ArrayList<Menu>()

    companion object {
        val instance by lazy { ListMenus() }
    }

    init {
        val favorite = Menu(MaterialDrawableBuilder.IconValue.HEART, "Favorite Playlist",
                false, R.color.color_red)

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