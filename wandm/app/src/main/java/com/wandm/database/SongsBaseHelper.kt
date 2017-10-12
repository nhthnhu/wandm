package com.wandm.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SongsBaseHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        private val DATABASE_NAME = "wandm.db"
        private val VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table " + SongsTable.TABLE_NAME + " (" +
                SongsTable.DATA + " text primary key, " +
                SongsTable.TITLE + " text, " +
                SongsTable.ALBUM_ID + " text, " +
                SongsTable.ALBUM_NAME + " text, " +
                SongsTable.ARTIST_ID + " text, " +
                SongsTable.ARTIST_NAME + " text, " +
                SongsTable.DURATION + " text, " +
                SongsTable.TRACK_NUMBER + " text, " +
                SongsTable.ID + " text)"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

}