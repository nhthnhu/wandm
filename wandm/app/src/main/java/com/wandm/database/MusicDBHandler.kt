package com.wandm.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.wandm.models.Playlist
import com.wandm.models.song.Song


class MusicDBHandler private constructor(context: Context, var tableName: String) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MusicDBHandler? = null

        fun getInstance(context: Context, tableName: String): MusicDBHandler? {
            if (instance == null) {
                instance = MusicDBHandler(context, tableName)
            }

            instance?.tableName = tableName
            return instance
        }
    }

    private var context: Context? = null
    var database: SQLiteDatabase? = null

    private val insetEvents = ArrayList<InsertEvent>()
    private val deleteEvents = ArrayList<DeleteEvent>()

    interface InsertEvent {
        fun onInsert(tableName: String)
    }

    interface DeleteEvent {
        fun onDelete(tableName: String)
    }

    fun setInsertEvent(e: InsertEvent) {
        insetEvents.add(e)
    }

    fun setDeleteEvent(e: DeleteEvent) {
        deleteEvents.add(e)
    }


    init {
        this.context = context.applicationContext
        database = MusicDBHelper(this.context).writableDatabase
    }

    fun insert(data: Any?): Boolean {
        var values: ContentValues? = null
        when (tableName) {
            FavoritesTable.TABLE_NAME -> {
                values = getFavoriteContentValues(data as Song)
            }

            PlaylistsTable.TABLE_NAME -> {
                values = getPlaylistContentValues(data as Playlist)
            }

            PlaylistSongsTable.TABLE_NAME -> {
                val song = data as Song
                val cursor = query(PlaylistSongsTable.Cols.DATA + "=? and " +
                        PlaylistSongsTable.Cols.PLAYLIST_ID + "=?",
                        arrayOf(song.data, song.playlistId.toString()))
                if (cursor != null && cursor.count > 0) return false

                values = getPlaylistSongContentValues(song)
            }
        }

        val row = database?.insert(tableName, null, values)
        if (row == (-1).toLong()) {
            return false
        }

        for (insertEvent in insetEvents) insertEvent.onInsert(tableName)
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
        val statement = "SELECT COUNT(*) FROM SongsPlayList WHERE playlist_id=?";

        val playlists = ArrayList<Playlist>()
        val cursor = query(null, null)
        try {
            cursor?.moveToFirst()
            if (cursor != null)
                while (!cursor.isAfterLast) {
                    val playlist = cursor.getPlaylist()

                    val countCursor = database?.rawQuery(statement, arrayOf(playlist.id.toString()))
                    countCursor?.moveToFirst()
                    val songCount = countCursor?.getInt(0)
                    playlist.songCount = songCount ?: 0
                    countCursor?.close()

                    playlists.add(playlist)
                    cursor.moveToNext()
                }
        } finally {
            cursor?.close()
        }
        return playlists
    }

    fun getPlaylistSongs(playlistId: Int): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val cursor = query("${PlaylistSongsTable.Cols.PLAYLIST_ID} = ?",
                arrayOf(playlistId.toString()))
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


    fun getPlaylistSong(playlistId: Int): Song? {
        val cursor = query("${PlaylistSongsTable.Cols.PLAYLIST_ID} = ?",
                arrayOf(playlistId.toString()))
        try {
            cursor?.moveToFirst()
            return cursor?.getPlaylistSong()
        } catch (e: Exception) {
            return null
        } finally {
            cursor?.close()
        }
    }

    fun getLatestPlaylist(): Playlist {

        val cursor = database?.query(PlaylistsTable.TABLE_NAME, null,
                null, null, null, null,
                PlaylistsTable.Cols.ID + " DESC")
        try {
            cursor?.moveToFirst()
            if (cursor != null && !cursor.isAfterLast) {
                val playlist = (MusicCursorWrapper(cursor)).getPlaylist()
                return playlist
            }
        } finally {
            cursor?.close()
        }

        return Playlist()
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

    fun removeSongFromPlaylist(song: Song) {
        database?.delete(tableName, PlaylistSongsTable.Cols.TITLE + "=?"
                , arrayOf(song.title))

        for (deleteEvent in deleteEvents) {
            deleteEvent.onDelete(tableName)
        }
    }

    fun removePlaylist(playlistId: Int) {
        database?.delete(PlaylistSongsTable.TABLE_NAME
                , PlaylistSongsTable.Cols.PLAYLIST_ID + "=?"
                , arrayOf(playlistId.toString()))

        database?.delete(tableName, PlaylistsTable.Cols.ID + "=?"
                , arrayOf(playlistId.toString()))

        for (deleteEvent in deleteEvents) {
            deleteEvent.onDelete(tableName)
        }

    }

    private fun query(whereClause: String?, whereArgs: Array<String>?): MusicCursorWrapper? {
        val cursor = database?.query(tableName,
                null, whereClause, whereArgs,
                null, null, null)
        return MusicCursorWrapper(cursor)
    }

}
