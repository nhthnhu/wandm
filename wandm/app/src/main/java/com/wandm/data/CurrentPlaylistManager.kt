package com.wandm.data

import android.util.Log
import com.wandm.models.song.Song
import com.wandm.utils.PreferencesUtils
import java.util.*

object CurrentPlaylistManager {
    val TAG = "CurrentPlaylistManager"

    val instance = CurrentPlaylistManager

    var listSongs = ArrayList<Song>()
    var position = 0
        set(value) {
            field = value
            currentSong = listSongs[value]
        }

    var currentSong: Song? = null

    fun next(): Song? {
        if (PreferencesUtils.getRepeatMode() == 2) {
            position = position
        } else if (PreferencesUtils.getShuffleMode()) {
            val random = Random()
            position = random.nextInt(listSongs.size)
        } else {
            if (position == listSongs.size - 1) {
                position = 0
            } else
                position++
        }

        currentSong = listSongs[position]
        Log.d(TAG, currentSong?.data)
        return currentSong
    }

    fun previous(): Song? {
        if (PreferencesUtils.getRepeatMode() == 2) {
            position = position
        } else if (PreferencesUtils.getShuffleMode()) {
            val random = Random()
            position = random.nextInt(listSongs.size)
        } else {
            if (position == 0) {
                position = listSongs.size - 1
            } else
                position--
        }

        currentSong = listSongs[position]
        Log.d(TAG, currentSong?.data)
        return currentSong
    }

}