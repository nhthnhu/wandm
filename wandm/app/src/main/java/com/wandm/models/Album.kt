/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

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
