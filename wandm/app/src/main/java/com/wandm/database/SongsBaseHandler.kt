package com.wandm.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.wandm.models.Song


class SongsBaseHandler private constructor(context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SongsBaseHandler? = null

        fun getInstance(context: Context): SongsBaseHandler? {
            if (instance == null) {
                instance = SongsBaseHandler(context)
            }
            return instance
        }
    }

    private var mFavoriteSongs: ArrayList<Song>? = null
    private var mContext: Context? = null
    private var mDatabase: SQLiteDatabase? = null

    init {
        mContext = context.applicationContext
        mDatabase = SongsBaseHelper(mContext!!).writableDatabase
    }

    fun addSong(song: Song): Boolean {
        val values = getContentValues(song)
        val row = mDatabase?.insert(SongsTable.TABLE_NAME, null, values)
        if (row == (-1).toLong()) {
            return false
        }
        return true
    }

    fun removeSong(song: Song): Boolean {
        val numberRows = mDatabase?.delete(SongsTable.TABLE_NAME,
                "${SongsTable.ID} = ?",
                arrayOf(song.id.toString()))
        if (numberRows == 0)
            return false
        return true
    }

    fun getList(): ArrayList<Song>? {
        mFavoriteSongs = ArrayList()
        val cursor = querySongs(null, null)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                mFavoriteSongs?.add(cursor.getSong())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }

        return mFavoriteSongs
    }

    fun getSong(url: String): Song? {
        val cursor = querySongs("${SongsTable.ID} = ?", arrayOf(url))
        try {
            cursor.moveToFirst()
            return cursor.getSong()
        } catch (e: Exception) {
            return null
        } finally {
            cursor.close()
        }
    }

    fun removeList() {
        mDatabase?.delete(SongsTable.TABLE_NAME, null, null)
    }

    private fun getContentValues(song: Song): ContentValues {
        val values = ContentValues()
        values.put(SongsTable.ID, song.id)
        values.put(SongsTable.TITLE, song.title)
        values.put(SongsTable.ALBUM_ID, song.albumId)
        values.put(SongsTable.ALBUM_NAME, song.albumName)
        values.put(SongsTable.ARTIST_ID, song.artistId)
        values.put(SongsTable.ARTIST_NAME, song.artistName)
        values.put(SongsTable.DURATION, song.duration)
        values.put(SongsTable.TRACK_NUMBER, song.trackNumber)
        values.put(SongsTable.DATA, song.data)

        return values
    }

    private fun querySongs(whereClause: String?, whereArgs: Array<String>?): SongCursorWrapper {
        val cursor = mDatabase?.query(
                SongsTable.TABLE_NAME, null,
                whereClause, whereArgs,
                null, null, null)

        return SongCursorWrapper(cursor!!)
    }


}
