package com.wandm.data

import com.wandm.models.Song
import com.wandm.utils.PreferencesUtils
import java.util.*

object CurrentPlaylistManager {

    val instance = CurrentPlaylistManager

    var mListSongs = ArrayList<Song>()
    var mPosition = 0
        set(value) {
            field = value
            mSong = mListSongs[value]
        }

    var mSong = Song()

    fun next(): Song {
        if (PreferencesUtils.getRepeatMode() == 2) {
            mPosition = mPosition
        } else if (PreferencesUtils.getShuffleMode()) {
            val random = Random()
            mPosition = random.nextInt(mListSongs.size)
        } else {
            if (mPosition == mListSongs.size - 1) {
                mPosition = 0
            } else
                mPosition++
        }

        mSong = mListSongs[mPosition]
        return mSong
    }

    fun previous(): Song {
        if (PreferencesUtils.getRepeatMode() == 2) {
            mPosition = mPosition
        } else if (PreferencesUtils.getShuffleMode()) {
            val random = Random()
            mPosition = random.nextInt(mListSongs.size)
        } else {
            if (mPosition == 0) {
                mPosition = mListSongs.size - 1
            } else
                mPosition--
        }

        mSong = mListSongs[mPosition]
        return mSong
    }

}