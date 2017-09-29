package com.wandm.models

class Album {
    val artistId: Long
    val artistName: String
    val id: Long
    val songCount: Int
    val title: String
    val year: Int

    constructor() {
        this.id = -1
        this.title = ""
        this.artistName = ""
        this.artistId = -1
        this.songCount = -1
        this.year = -1
    }

    constructor(_id: Long, _title: String, _artistName: String, _artistId: Long, _songCount: Int, _year: Int) {
        this.id = _id
        this.title = _title
        this.artistName = _artistName
        this.artistId = _artistId
        this.songCount = _songCount
        this.year = _year
    }


}
