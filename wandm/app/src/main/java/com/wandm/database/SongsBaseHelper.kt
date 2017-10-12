package com.wandm.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SongsBaseHelper(val context: Context, val tableName: String) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        private val DATABASE_NAME = "wandm.db"
        private val VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table " + tableName + " (" +
                FavoritesTable.DATA + " text primary key, " +
                FavoritesTable.TITLE + " text, " +
                FavoritesTable.ALBUM_ID + " text, " +
                FavoritesTable.ALBUM_NAME + " text, " +
                FavoritesTable.ARTIST_ID + " text, " +
                FavoritesTable.ARTIST_NAME + " text, " +
                FavoritesTable.DURATION + " text, " +
                FavoritesTable.TRACK_NUMBER + " text, " +
                FavoritesTable.ID + " text)"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

}