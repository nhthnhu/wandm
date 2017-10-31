package com.wandm.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.wandm.models.song.Song


class SongsBaseHandler private constructor(context: Context, val tableName: String) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SongsBaseHandler? = null

        fun getInstance(context: Context, tableName: String): SongsBaseHandler? {
            if (instance == null) {
                instance = SongsBaseHandler(context, tableName)
            }
            return instance
        }
    }

    private var mSongs: ArrayList<Song>? = null
    private var mContext: Context? = null
    private var mDatabase: SQLiteDatabase? = null

    init {
        mContext = context.applicationContext
        mDatabase = SongsBaseHelper(mContext!!, tableName).writableDatabase
    }

    fun addSong(song: Song): Boolean {
        val values = getContentValues(song)
        val row = mDatabase?.insert(tableName, null, values)
        if (row == (-1).toLong()) {
            return false
        }
        return true
    }

    fun removeSong(song: Song): Boolean {
        val numberRows = mDatabase?.delete(tableName,
                "${FavoritesTable.DATA} = ?",
                arrayOf(song.data.toString()))
        if (numberRows == 0)
            return false
        return true
    }

    fun getList(): ArrayList<Song>? {
        mSongs = ArrayList()
        val cursor = querySongs(null, null)
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                mSongs?.add(cursor.getSong())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }

        return mSongs
    }

    fun getSong(data: String): Song? {
        val cursor = querySongs("${FavoritesTable.DATA} = ?", arrayOf(data))
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
        mDatabase?.delete(tableName, null, null)
    }

    private fun getContentValues(song: Song): ContentValues {
        val values = ContentValues()
        values.put(FavoritesTable.ID, song.id)
        values.put(FavoritesTable.TITLE, song.title)
        values.put(FavoritesTable.ALBUM_ID, song.albumId)
        values.put(FavoritesTable.ALBUM_NAME, song.albumName)
        values.put(FavoritesTable.ARTIST_ID, song.artistId)
        values.put(FavoritesTable.ARTIST_NAME, song.artistName)
        values.put(FavoritesTable.DURATION, song.duration)
        values.put(FavoritesTable.TRACK_NUMBER, song.trackNumber)
        values.put(FavoritesTable.DATA, song.data)

        return values
    }

    private fun querySongs(whereClause: String?, whereArgs: Array<String>?): SongCursorWrapper {
        val cursor = mDatabase?.query(
                tableName, null,
                whereClause, whereArgs,
                null, null, null)

        return SongCursorWrapper(cursor!!)
    }


}
