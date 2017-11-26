package com.wandm.models.song

import java.io.Serializable

class Song : Serializable {
    var id: Long = -1
    var albumId: Long = -1
    var artistId: Long = 0
    var title: String = ""
    var artistName: String = ""
    var albumName: String = ""
    var duration: Int = -1
    var trackNumber: Int = -1
    var data: String = ""
    var albumArt: String = ""
    var downloadEnable: Boolean = false
    var art: ByteArray? = null
    var playlistId = 0


    constructor() {
        this.id = -1
        this.albumId = -1
        this.artistId = -1
        this.title = ""
        this.artistName = ""
        this.albumName = ""
        this.duration = -1
        this.trackNumber = -1
        this.data = ""
        this.albumArt = ""
        this.downloadEnable = false
    }

    constructor(_title: String, _url: String, _albumArt: String) {
        this.data = _url
        this.albumArt = _albumArt
        this.title = _title
        this.downloadEnable = true
    }

    constructor(_id: Long, _albumId: Long, _artistId: Long, _title: String, _artistName: String,
                _albumName: String, _duration: Int, _trackNumber: Int, _data: String) {
        this.id = _id
        this.albumId = _albumId
        this.artistId = _artistId
        this.title = _title
        this.artistName = _artistName
        this.albumName = _albumName
        this.duration = _duration
        this.trackNumber = _trackNumber
        this.data = _data
        this.downloadEnable = false
    }

    constructor(_id: Long, _albumId: Long, _artistId: Long, _title: String, _artistName: String,
                _albumName: String, _duration: Int, _trackNumber: Int, _data: String, _playlist_id: Int) {
        this.id = _id
        this.albumId = _albumId
        this.artistId = _artistId
        this.title = _title
        this.artistName = _artistName
        this.albumName = _albumName
        this.duration = _duration
        this.trackNumber = _trackNumber
        this.data = _data
        this.downloadEnable = false
        this.playlistId = playlistId
    }
}
