package com.wandm.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.wandm.models.playlist.Playlist
import com.wandm.models.song.Song


class MusicDBHandler private constructor(context: Context, private val tableName: String) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MusicDBHandler? = null
        private var tableName = FavoritesTable.TABLE_NAME

        fun getInstance(context: Context, tableName: String): MusicDBHandler? {
            this.tableName = tableName
            if (instance == null) {
                instance = MusicDBHandler(context, tableName)
            }
            return instance
        }
    }

    private var context: Context? = null
    private var database: SQLiteDatabase? = null

    private var event: InsertEvent? = null

    interface InsertEvent {
        fun onInsert(tableName: String)
    }

    fun setInsertEvent(e: InsertEvent) {
        event = e
    }

    init {
        this.context = context.applicationContext
        database = MusicDBHelper(this.context).writableDatabase
    }

    fun insert(data: Any): Boolean {
        var values: ContentValues? = null
        when (tableName) {
            FavoritesTable.TABLE_NAME -> {
                values = getFavoriteContentValues(data as Song)
            }

            PlaylistsTable.TABLE_NAME -> {
                values = getPlaylistContentValues(data as Playlist)
            }

            PlaylistSongsTable.TABLE_NAME -> {
                values = getPlaylistSongContentValues(data as Song)
            }
        }


        val row = database?.insert(tableName, null, values)
        if (row == (-1).toLong()) {
            return false
        }

        event?.onInsert(tableName)
        return true
    }

    fun remove(data: Any): Boolean {
        when (tableName) {
            FavoritesTable.TABLE_NAME -> {
                val numberRows = database?.delete(tableName,
                        "${FavoritesTable.Cols.DATA} = ?",
                        arrayOf((data as Song).data))

                if (numberRows == 0)
                    return false
            }

            PlaylistsTable.TABLE_NAME -> {
                val numberRows = database?.delete(tableName,
                        "${PlaylistsTable.Cols.ID} = ?",
                        arrayOf((data as Playlist).id.toString()))

                if (numberRows == 0)
                    return false
            }

            PlaylistSongsTable.TABLE_NAME -> {
                val numberRows = database?.delete(tableName,
                        "${PlaylistSongsTable.Cols.DATA} = ?",
                        arrayOf((data as Song).data))

                if (numberRows == 0)
                    return false
            }
        }

        return true
    }

    fun getFavorites(): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val cursor = query(null, null)
        try {
            cursor?.moveToFirst()
            if (cursor != null)
                while (!cursor.isAfterLast) {
                    songs.add(cursor.getFavoriteSong())
                    cursor.moveToNext()
                }
        } finally {
            cursor?.close()
        }
        return songs
    }

    fun getPlaylists(): ArrayList<Playlist> {
        val playlists = ArrayList<Playlist>()
        val cursor = query(null, null)
        try {
            cursor?.moveToFirst()
            if (cursor != null)
                while (!cursor.isAfterLast) {
                    playlists.add(cursor.getPlaylist())
                    cursor.moveToNext()
                }
        } finally {
            cursor?.close()
        }
        return playlists
    }

    fun getPlaylistSongs(): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val cursor = query(null, null)
        try {
            cursor?.moveToFirst()
            if (cursor != null)
                while (!cursor.isAfterLast) {
                    songs.add(cursor.getPlaylistSong())
                    cursor.moveToNext()
                }
        } finally {
            cursor?.close()
        }
        return songs
    }

    fun getFavoriteSong(data: String): Song? {
        val cursor = query("${FavoritesTable.Cols.DATA} = ?", arrayOf(data))
        try {
            cursor?.moveToFirst()
            return cursor?.getFavoriteSong()
        } catch (e: Exception) {
            return null
        } finally {
            cursor?.close()
        }
    }

    fun getPlaylist(id: Int): Playlist? {
        val cursor = query("${PlaylistsTable.Cols.ID} = ?", arrayOf(id.toString()))
        try {
            cursor?.moveToFirst()
            return cursor?.getPlaylist()
        } catch (e: Exception) {
            return null
        } finally {
            cursor?.close()
        }
    }


    fun getPlaylistSong(data: String): Song? {
        val cursor = query("${PlaylistSongsTable.Cols.DATA} = ?", arrayOf(data))
        try {
            cursor?.moveToFirst()
            return cursor?.getPlaylistSong()
        } catch (e: Exception) {
            return null
        } finally {
            cursor?.close()
        }
    }

    fun deleteAll() {
        database?.delete(tableName, null, null)
    }

    private fun getFavoriteContentValues(song: Song): ContentValues {
        val values = ContentValues()
        values.put(FavoritesTable.Cols.ID, song.id)
        values.put(FavoritesTable.Cols.TITLE, song.title)
        values.put(FavoritesTable.Cols.ALBUM_ID, song.albumId)
        values.put(FavoritesTable.Cols.ALBUM_NAME, song.albumName)
        values.put(FavoritesTable.Cols.ARTIST_ID, song.artistId)
        values.put(FavoritesTable.Cols.ARTIST_NAME, song.artistName)
        values.put(FavoritesTable.Cols.DURATION, song.duration)
        values.put(FavoritesTable.Cols.TRACK_NUMBER, song.trackNumber)
        values.put(FavoritesTable.Cols.DATA, song.data)

        return values
    }

    private fun getPlaylistContentValues(playlist: Playlist): ContentValues {
        val values = ContentValues()
        values.put(PlaylistsTable.Cols.ID, playlist.id)
        values.put(PlaylistsTable.Cols.NAME, playlist.name)
        values.put(PlaylistsTable.Cols.SONGS_COUNT, playlist.songCount)

        return values
    }

    private fun getPlaylistSongContentValues(song: Song): ContentValues {
        val values = ContentValues()
        values.put(PlaylistSongsTable.Cols.ID, song.id)
        values.put(PlaylistSongsTable.Cols.TITLE, song.title)
        values.put(PlaylistSongsTable.Cols.ALBUM_ID, song.albumId)
        values.put(PlaylistSongsTable.Cols.ALBUM_NAME, song.albumName)
        values.put(PlaylistSongsTable.Cols.ARTIST_ID, song.artistId)
        values.put(PlaylistSongsTable.Cols.ARTIST_NAME, song.artistName)
        values.put(PlaylistSongsTable.Cols.DURATION, song.duration)
        values.put(PlaylistSongsTable.Cols.TRACK_NUMBER, song.trackNumber)
        values.put(PlaylistSongsTable.Cols.DATA, song.data)
        values.put(PlaylistSongsTable.Cols.PLAYLIST_ID, song.playlistId)

        return values
    }

    @SuppressLint("Recycle")
    private fun query(whereClause: String?, whereArgs: Array<String>?): MusicCursorWrapper? {
        val cursor = database?.query(tableName,
                null, whereClause, whereArgs,
                null, null, null)
        return MusicCursorWrapper(cursor)
    }

}
