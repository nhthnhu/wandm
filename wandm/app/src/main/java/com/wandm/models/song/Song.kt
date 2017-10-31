package com.wandm.models.song

class Song {
    var albumId: Long = -1
    var albumName: String = ""
    var artistId: Long = 0
    var artistName: String = ""
    var duration: Int = -1
    var id: Long = -1
    var title: String = ""
    var trackNumber: Int = -1
    var data: String = ""
    var albumArt: String = ""


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
    }

    constructor(_title: String, _url: String, _albumArt: String) {
        this.data = _url
        this.albumArt = _albumArt
        this.title = _title
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
    }

}
