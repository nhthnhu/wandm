package com.wandm.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MusicDBHelper(val context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        private val DATABASE_NAME = "wandm.db"
        private val VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table " + FavoritesTable.TABLE_NAME + " (" +
                FavoritesTable.Cols.DATA + " text primary key, " +
                FavoritesTable.Cols.TITLE + " text, " +
                FavoritesTable.Cols.ALBUM_ID + " text, " +
                FavoritesTable.Cols.ALBUM_NAME + " text, " +
                FavoritesTable.Cols.ARTIST_ID + " text, " +
                FavoritesTable.Cols.ARTIST_NAME + " text, " +
                FavoritesTable.Cols.DURATION + " text, " +
                FavoritesTable.Cols.TRACK_NUMBER + " text, " +
                FavoritesTable.Cols.ID + " text)"
        )

        p0?.execSQL("create table " + PlaylistsTable.TABLE_NAME + " (" +
                PlaylistsTable.Cols.ID + " integer primary key autoincrement, " +
                PlaylistsTable.Cols.NAME + " text, " +
                PlaylistsTable.Cols.SONGS_COUNT + " integer)"
        )

        p0?.execSQL("create table " + PlaylistSongsTable.TABLE_NAME + " (" +
                "_id integer primary key autoincrement," +
                PlaylistSongsTable.Cols.DATA + " text, " +
                PlaylistSongsTable.Cols.TITLE + " text, " +
                PlaylistSongsTable.Cols.ALBUM_ID + " text, " +
                PlaylistSongsTable.Cols.ALBUM_NAME + " text, " +
                PlaylistSongsTable.Cols.ARTIST_ID + " text, " +
                PlaylistSongsTable.Cols.ARTIST_NAME + " text, " +
                PlaylistSongsTable.Cols.DURATION + " text, " +
                PlaylistSongsTable.Cols.TRACK_NUMBER + " text, " +
                PlaylistSongsTable.Cols.ID + " text," +
                PlaylistSongsTable.Cols.PLAYLIST_ID + " integer)"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

}