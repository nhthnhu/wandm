package com.wandm.database

import android.database.Cursor
import android.database.CursorWrapper
import com.wandm.models.Song


class SongCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getSong(): Song {
        val id = getString(getColumnIndex(FavoritesTable.ID))
        val title = getString(getColumnIndex(FavoritesTable.TITLE))
        val albumId = getString(getColumnIndex(FavoritesTable.ALBUM_ID))
        val albumName = getString(getColumnIndex(FavoritesTable.ALBUM_NAME))
        val artistId = getString(getColumnIndex(FavoritesTable.ARTIST_ID))
        val artistName = getString(getColumnIndex(FavoritesTable.ARTIST_NAME))
        val duration = getString(getColumnIndex(FavoritesTable.DURATION))
        val trackNumber = getString(getColumnIndex(FavoritesTable.TRACK_NUMBER))
        val data = getString(getColumnIndex(FavoritesTable.DATA))

        return Song(id.toLong(), albumId.toLong(), artistId.toLong(), title, artistName, albumName,
                duration.toInt(), trackNumber.toInt(), data)
    }
}