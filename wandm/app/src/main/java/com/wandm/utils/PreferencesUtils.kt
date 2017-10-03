package com.wandm.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.os.Environment
import android.preference.PreferenceManager
import com.wandm.App


class PreferencesUtils(context: Context) {
    companion object {

        val SONG_SORT_ORDER = "song_sort_order"
        private val LAST_FOLDER = "last_folder"
        private val ARTIST_IMAGE = "artist_image"
        private val ARTIST_IMAGE_MOBILE = "artist_image_mobile"
        val ENABLE_SHUFFLE = "enable_shuffle"
        val ENABLE_REPEAT = "enable_repeat"

        val instance by lazy { PreferencesUtils(App.instance) }

        private lateinit var mPreferences: SharedPreferences
    }

    private var connManager: ConnectivityManager? = null

    val songSortOrder: String
        get() = mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z)

    val lastFolder: String
        get() = mPreferences.getString(LAST_FOLDER, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)

    init {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }


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

    fun storeRepeatState(state: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(ENABLE_REPEAT, state)
        editor.apply()
    }

    fun getRepeatState(): Boolean {
        return mPreferences.getBoolean(ENABLE_REPEAT, false)
    }

    fun storeShuffleState(state: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(ENABLE_SHUFFLE, state)
        editor.apply()
    }

    fun getShuffleState(): Boolean {
        return mPreferences.getBoolean(ENABLE_SHUFFLE, false)
    }
}

