package com.wandm.data

import com.wandm.models.Song
import java.util.*

object CurrentPlaylistManager {

    val instance = CurrentPlaylistManager

    var mListSongs = ArrayList<Song>()
    var mSong = Song()
    var mPosition = 0

    fun next(): Song {
        if (mPosition == mListSongs.size - 1) {
            mPosition = 0
        } else
            mPosition++

        mSong = mListSongs[mPosition]
        return mSong
    }

    fun previous(): Song {
        if (mPosition == 0) {
            mPosition = mListSongs.size - 1
        } else
            mPosition--

        mSong = mListSongs[mPosition]
        return mSong
    }

}