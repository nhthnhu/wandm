package com.wandm.models.playlist

class Playlist {

    var id: Int
    var name: String
    var songCount: Int

    constructor() {
        this.id = -1
        this.name = ""
        this.songCount = 0
    }

    constructor(_id: Int, _name: String, _songCount: Int) {
        this.id = _id
        this.name = _name
        this.songCount = _songCount
    }

    constructor(_name: String) {
        this.name = _name
        this.id = -1
        this.songCount = 0
    }
}
