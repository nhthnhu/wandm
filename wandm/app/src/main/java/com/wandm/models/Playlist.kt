package com.wandm.models

class Playlist {

    val id: Long
    val name: String
    val songCount: Int

    constructor() {
        this.id = -1
        this.name = ""
        this.songCount = -1
    }

    constructor(_id: Long, _name: String, _songCount: Int) {
        this.id = _id
        this.name = _name
        this.songCount = _songCount
    }
}
