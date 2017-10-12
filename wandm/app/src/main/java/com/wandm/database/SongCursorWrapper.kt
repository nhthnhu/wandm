package com.wandm.database

import android.database.Cursor
import android.database.CursorWrapper
import com.wandm.models.Song


class SongCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getSong(): Song {
        val id = getString(getColumnIndex(SongsTable.ID))
        val title = getString(getColumnIndex(SongsTable.TITLE))
        val albumId = getString(getColumnIndex(SongsTable.ALBUM_ID))
        val albumName = getString(getColumnIndex(SongsTable.ALBUM_NAME))
        val artistId = getString(getColumnIndex(SongsTable.ARTIST_ID))
        val artistName = getString(getColumnIndex(SongsTable.ARTIST_NAME))
        val duration = getString(getColumnIndex(SongsTable.DURATION))
        val trackNumber = getString(getColumnIndex(SongsTable.TRACK_NUMBER))
        val data = getString(getColumnIndex(SongsTable.DATA))

        return Song(id.toLong(), albumId.toLong(), artistId.toLong(), title, artistName, albumName,
                duration.toInt(), trackNumber.toInt(), data)
    }
}