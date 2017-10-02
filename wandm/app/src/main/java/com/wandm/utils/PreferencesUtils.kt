package com.wandm.utils

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.os.Environment
import android.preference.PreferenceManager
import com.wandm.App


object PreferencesUtils {
    val SONG_SORT_ORDER = "song_sort_order"
    val ALBUM_SORT_ORDER = "album_sort_order"
    val ALBUM_SONG_SORT_ORDER = "album_song_sort_order"
    private val LAST_FOLDER = "last_folder"
    private val ARTIST_IMAGE = "artist_image"
    private val ARTIST_IMAGE_MOBILE = "artist_image_mobile"

    private val mPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance)

    private var connManager: ConnectivityManager? = null

    val songSortOrder: String
        get() = mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z)

    val lastFolder: String
        get() = mPreferences.getString(LAST_FOLDER, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)


    fun setOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener)
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

    fun getAlbumSortOrder(): String? {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z)
    }

    fun getAlbumSongSortOrder(): String? {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST)
    }
}

