package com.wandm.models.menu

import com.wandm.App
import com.wandm.R
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

data class Menu(val icon: MaterialDrawableBuilder.IconValue, val content: String) {
    companion object {
        val SONGS = App.instance.resources.getString(R.string.songs)
        val FAVORITES = App.instance.resources.getString(R.string.favorites)
        val ARTISTS = App.instance.resources.getString(R.string.artists)
        val ALBUMS = App.instance.resources.getString(R.string.albums)
        val FOLDERS = App.instance.resources.getString(R.string.folders)
    }
}