/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.wandm.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.os.Environment
import android.preference.PreferenceManager
import com.wandm.App


class PreferencesUtility(context: Context) {
    private var connManager: ConnectivityManager? = null

    val animations: Boolean
        get() = mPreferences.getBoolean(TOGGLE_ANIMATIONS, true)

    val systemAnimations: Boolean
        get() = mPreferences.getBoolean(TOGGLE_SYSTEM_ANIMATIONS, true)

    var isArtistsInGrid: Boolean
        get() = mPreferences.getBoolean(TOGGLE_ARTIST_GRID, true)
        set(b) {
            val editor = mPreferences.edit()
            editor.putBoolean(TOGGLE_ARTIST_GRID, b)
            editor.apply()
        }

    var isAlbumsInGrid: Boolean
        get() = mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true)
        set(b) {
            val editor = mPreferences.edit()
            editor.putBoolean(TOGGLE_ALBUM_GRID, b)
            editor.apply()
        }

    val theme: String
        get() = mPreferences.getString(THEME_PREFERNCE, "light")

    val xPosedTrackselectorEnabled: Boolean
        get() = mPreferences.getBoolean(TOGGLE_XPOSED_TRACKSELECTOR, false)

    var playlistView: Int
        get() = mPreferences.getInt(TOGGLE_PLAYLIST_VIEW, 0)
        set(i) {
            val editor = mPreferences.edit()
            editor.putInt(TOGGLE_PLAYLIST_VIEW, i)
            editor.apply()
        }

    val songSortOrder: String
        get() = mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z)

    /**
     * @parm lastAddedMillis timestamp in millis used as a cutoff for last added playlist
     */
    var lastAddedCutoff: Long
        get() = mPreferences.getLong(LAST_ADDED_CUTOFF, 0L)
        set(lastAddedMillis) = mPreferences.edit().putLong(LAST_ADDED_CUTOFF, lastAddedMillis).apply()

    val isGesturesEnabled: Boolean
        get() = mPreferences.getBoolean(GESTURES, true)

    val lastFolder: String
        get() = mPreferences.getString(LAST_FOLDER, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)

    init {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }


    fun setOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun pauseEnabledOnDetach(): Boolean {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true)
    }

    fun didNowplayingThemeChanged(): Boolean {
        return mPreferences.getBoolean(NOW_PLAYNG_THEME_VALUE, false)
    }

    fun setNowPlayingThemeChanged(value: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(NOW_PLAYNG_THEME_VALUE, value)
        editor.apply()
    }

    fun showAutoPlaylist(): Boolean {
        return mPreferences.getBoolean(TOGGLE_SHOW_AUTO_PLAYLIST, true)
    }

    fun setToggleShowAutoPlaylist(b: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(TOGGLE_SHOW_AUTO_PLAYLIST, b)
        editor.apply()
    }

    fun storeLastFolder(path: String) {
        val editor = mPreferences.edit()
        editor.putString(LAST_FOLDER, path)
        editor.apply()
    }

    fun loadArtistImages(): Boolean {
        if (mPreferences.getBoolean(ARTIST_IMAGE, true)) {
            if (!mPreferences.getBoolean(ARTIST_IMAGE_MOBILE, false)) {
                if (connManager == null)
                    connManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val ni = connManager?.activeNetworkInfo
                return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
            }
            return true
        }
        return false
    }

    companion object {

        val ARTIST_SORT_ORDER = "artist_sort_order"
        val ARTIST_SONG_SORT_ORDER = "artist_song_sort_order"
        val ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order"
        val ALBUM_SORT_ORDER = "album_sort_order"
        val ALBUM_SONG_SORT_ORDER = "album_song_sort_order"
        val SONG_SORT_ORDER = "song_sort_order"
        private val NOW_PLAYING_SELECTOR = "now_paying_selector"
        private val TOGGLE_ANIMATIONS = "toggle_animations"
        private val TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations"
        private val TOGGLE_ARTIST_GRID = "toggle_artist_grid"
        private val TOGGLE_ALBUM_GRID = "toggle_album_grid"
        private val TOGGLE_PLAYLIST_VIEW = "toggle_playlist_view"
        private val TOGGLE_SHOW_AUTO_PLAYLIST = "toggle_show_auto_playlist"
        private val LAST_FOLDER = "last_folder"

        private val TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause"
        private val THEME_PREFERNCE = "theme_preference"
        private val START_PAGE_INDEX = "start_page_index"
        private val START_PAGE_PREFERENCE_LASTOPENED = "start_page_preference_latopened"
        private val NOW_PLAYNG_THEME_VALUE = "now_playing_theme_value"
        private val TOGGLE_XPOSED_TRACKSELECTOR = "toggle_xposed_trackselector"
        val LAST_ADDED_CUTOFF = "last_added_cutoff"
        val GESTURES = "gestures"

        val FULL_UNLOCKED = "full_version_unlocked"

        private val SHOW_LOCKSCREEN_ALBUMART = "show_albumart_lockscreen"
        private val ARTIST_IMAGE = "artist_image"
        private val ARTIST_IMAGE_MOBILE = "artist_image_mobile"

        private var sInstance: PreferencesUtility? = null

        private lateinit var mPreferences: SharedPreferences

        fun getInstance(context: Context): PreferencesUtility {
            if (sInstance == null) {
                sInstance = PreferencesUtility(context.applicationContext)
            }
            return sInstance as PreferencesUtility
        }
    }
}

