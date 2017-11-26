package com.wandm.database

import android.database.Cursor
import android.database.CursorWrapper
import com.wandm.models.playlist.Playlist
import com.wandm.models.song.Song


class MusicCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {

    fun getFavoriteSong(): Song {
        val id = getString(getColumnIndex(FavoritesTable.Cols.ID))
        val title = getString(getColumnIndex(FavoritesTable.Cols.TITLE))
        val albumId = getString(getColumnIndex(FavoritesTable.Cols.ALBUM_ID))
        val albumName = getString(getColumnIndex(FavoritesTable.Cols.ALBUM_NAME))
        val artistId = getString(getColumnIndex(FavoritesTable.Cols.ARTIST_ID))
        val artistName = getString(getColumnIndex(FavoritesTable.Cols.ARTIST_NAME))
        val duration = getString(getColumnIndex(FavoritesTable.Cols.DURATION))
        val trackNumber = getString(getColumnIndex(FavoritesTable.Cols.TRACK_NUMBER))
        val data = getString(getColumnIndex(FavoritesTable.Cols.DATA))

        return Song(id.toLong(), albumId.toLong(), artistId.toLong(), title, artistName, albumName,
                duration.toInt(), trackNumber.toInt(), data)
    }

    fun getPlaylistSong(): Song {
        val id = getString(getColumnIndex(PlaylistSongsTable.Cols.ID))
        val title = getString(getColumnIndex(PlaylistSongsTable.Cols.TITLE))
        val albumId = getString(getColumnIndex(PlaylistSongsTable.Cols.ALBUM_ID))
        val albumName = getString(getColumnIndex(PlaylistSongsTable.Cols.ALBUM_NAME))
        val artistId = getString(getColumnIndex(PlaylistSongsTable.Cols.ARTIST_ID))
        val artistName = getString(getColumnIndex(PlaylistSongsTable.Cols.ARTIST_NAME))
        val duration = getString(getColumnIndex(PlaylistSongsTable.Cols.DURATION))
        val trackNumber = getString(getColumnIndex(PlaylistSongsTable.Cols.TRACK_NUMBER))
        val data = getString(getColumnIndex(PlaylistSongsTable.Cols.DATA))
        val playlistId = getInt(getColumnIndex(PlaylistSongsTable.Cols.PLAYLIST_ID))

        return Song(id.toLong(), albumId.toLong(), artistId.toLong(), title, artistName, albumName,
                duration.toInt(), trackNumber.toInt(), data, playlistId)
    }

    fun getPlaylist(): Playlist {
        val id = getInt(getColumnIndex(PlaylistsTable.Cols.ID))
        val name = getString(getColumnIndex(PlaylistsTable.Cols.NAME))
        val songCount = getInt(getColumnIndex(PlaylistsTable.Cols.SONGS_COUNT))

        return Playlist(id, name, songCount)
    }
}