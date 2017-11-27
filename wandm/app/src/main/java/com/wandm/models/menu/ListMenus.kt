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
        val songs = Menu(MaterialDrawableBuilder.IconValue.MUSIC_NOTE, Menu.SONGS)

        val favorite = Menu(MaterialDrawableBuilder.IconValue.HEART, Menu.FAVORITES)

        val playlist = Menu(MaterialDrawableBuilder.IconValue.PLAYLIST_PLUS, Menu.PLAYLIST)

        val artists = Menu(MaterialDrawableBuilder.IconValue.ACCOUNT, Menu.ARTISTS)

        val albums = Menu(MaterialDrawableBuilder.IconValue.ALBUM, Menu.ALBUMS)

        val folders = Menu(MaterialDrawableBuilder.IconValue.FOLDER, Menu.FOLDERS)

        mList.add(songs)
        mList.add(favorite)
        mList.add(playlist)
        mList.add(artists)
        mList.add(albums)
        mList.add(folders)
    }

    operator fun get(position: Int): Menu {
        return mList[position]
    }

    fun size() = mList.size
}