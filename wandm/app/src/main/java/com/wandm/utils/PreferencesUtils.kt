package com.wandm.utils

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.os.Environment
import android.preference.PreferenceManager
import com.wandm.App


object PreferencesUtils {
    val SONG_SORT_ORDER = "song_sort_order"
    val ARTIST_SORT_ORDER = "artist_sort_order"
    val ARTIST_SONG_SORT_ORDER = "artist_song_sort_order"
    val ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order"
    val ALBUM_SORT_ORDER = "album_sort_order"
    val ALBUM_SONG_SORT_ORDER = "album_song_sort_order"

    private val REPEAT_MODE = "repeat_mode"
    private val SHUFFLE_MODE = "shuffle_mode"

    private val LAST_FOLDER = "last_folder"
    private val ARTIST_IMAGE = "artist_image"
    private val ARTIST_IMAGE_MOBILE = "artist_image_mobile"

    val PREFS_THEME = "key_preference_theme"

    private val CURRENT_CATEGORY_POSITION = "current_category_position"

    val ALARM_SET = "alarm_set"

    val mPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance)

    private var connManager: ConnectivityManager? = null

    fun setOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun getLastFolder(): String {
        return mPreferences.getString(LAST_FOLDER, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)
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

    private fun setSortOrder(key: String, value: String) {
        val editor = mPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getArtistSortOrder(): String? {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z)
    }

    fun setArtistSortOrder(value: String) {
        setSortOrder(ARTIST_SORT_ORDER, value)
    }

    fun getArtistSongSortOrder(): String? {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z)
    }

    fun setArtistSongSortOrder(value: String) {
        setSortOrder(ARTIST_SONG_SORT_ORDER, value)
    }

    fun getArtistAlbumSortOrder(): String? {
        return mPreferences.getString(ARTIST_ALBUM_SORT_ORDER,
                SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z)
    }

    fun setArtistAlbumSortOrder(value: String) {
        setSortOrder(ARTIST_ALBUM_SORT_ORDER, value)
    }

    fun getAlbumSortOrder(): String? {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z)
    }

    fun setAlbumSortOrder(value: String) {
        setSortOrder(ALBUM_SORT_ORDER, value)
    }

    fun getAlbumSongSortOrder(): String? {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST)
    }

    fun setAlbumSongSortOrder(value: String) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value)
    }

    fun getSongSortOrder(): String? {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z)
    }

    fun setSongSortOrder(value: String) {
        setSortOrder(SONG_SORT_ORDER, value)
    }

    fun setShuffleMode(value: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(SHUFFLE_MODE, value)
        editor.apply()
    }

    fun getShuffleMode(): Boolean {
        return mPreferences.getBoolean(SHUFFLE_MODE, false)
    }

    fun setRepeatMode(value: Int) {
        val editor = mPreferences.edit()
        editor.putInt(REPEAT_MODE, value)
        editor.apply()
    }

    fun getRepeatMode(): Int {
        return mPreferences.getInt(REPEAT_MODE, 0)
    }

    fun setAlarm(value: String) {
        val editor = mPreferences.edit()
        editor.putString(ALARM_SET, value)
        editor.apply()
    }

    fun getAlarm(): String {
        return mPreferences.getString(ALARM_SET, "")
    }

    fun getLightTheme(): Boolean {
        return mPreferences.getBoolean(PREFS_THEME, false)
    }

    fun setCurrentCategory(value: Int) {
        val editor = mPreferences.edit()
        editor.putInt(CURRENT_CATEGORY_POSITION, value)
        editor.apply()
    }

    fun getCurrentCategory(): Int {
        return mPreferences.getInt(CURRENT_CATEGORY_POSITION, 0)
    }

}

