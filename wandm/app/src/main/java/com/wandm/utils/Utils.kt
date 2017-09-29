package com.wandm.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import java.io.File

object Utils {

    fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
    }

    fun getAlbumArtForFile(filePath: String): String {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(filePath)

        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    }

    fun makeLabel(context: Context, pluralInt: Int,
                  number: Int): String {
        return context.resources.getQuantityString(pluralInt, number, number)
    }

    fun getBlackWhiteColor(color: Int): Int {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness >= 0.5) {
            Color.WHITE
        } else
            Color.BLACK
    }

    enum class IdType private constructor(val mId: Int) {
        NA(0),
        Artist(1),
        Album(2),
        Playlist(3);


        companion object {

            fun getTypeById(id: Int): IdType {
                for (type in values()) {
                    if (type.mId == id) {
                        return type
                    }
                }

                throw IllegalArgumentException("Unrecognized id: " + id)
            }
        }
    }


    fun removeFromPlaylist(context: Context, id: Long,
                           playlistId: Long) {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val resolver = context.contentResolver
        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = ? ", arrayOf(java.lang.Long.toString(id)))
    }

    fun shareTrack(context: Context, id: Long) {

        val projection = arrayOf(BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM_ID)
        val selection = StringBuilder()
        selection.append(BaseColumns._ID + " IN (")
        selection.append(id)
        selection.append(")")
        val c = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(), null, null) ?: return

        c.moveToFirst()
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "audio/*"
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(c.getString(1))))
            context.startActivity(Intent.createChooser(share, "Share"))
            c.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
