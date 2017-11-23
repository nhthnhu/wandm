package com.wandm.models.menu

import com.wandm.App
import com.wandm.R
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder


class ListMenus private constructor() {
    private val mList = ArrayList<Menu>()

    companion object {
        val instance by lazy { ListMenus() }
    }

    init {
        val songs = Menu(MaterialDrawableBuilder.IconValue.MUSIC_NOTE, Menu.SONGS,
                R.color.color_white)

        val favorite = Menu(MaterialDrawableBuilder.IconValue.HEART, Menu.FAVORITES,
                R.color.color_red)

        val artists = Menu(MaterialDrawableBuilder.IconValue.ACCOUNT, Menu.ARTISTS,
                R.color.color_white)

        val albums = Menu(MaterialDrawableBuilder.IconValue.ALBUM, Menu.ALBUMS,
                R.color.color_white)

        val folders = Menu(MaterialDrawableBuilder.IconValue.FOLDER, Menu.FOLDERS,
                R.color.color_white)

        mList.add(songs)
        mList.add(favorite)
        mList.add(artists)
        mList.add(albums)
        mList.add(folders)
    }

    operator fun get(position: Int): Menu {
        return mList[position]
    }

    fun size() = mList.size
}