package com.wandm.utils

import android.content.ContentUris
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build

object Utils {

    fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
    }

    fun getAlbumArtForFile(filePath: String): String {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(filePath)

        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    }

    fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

}
