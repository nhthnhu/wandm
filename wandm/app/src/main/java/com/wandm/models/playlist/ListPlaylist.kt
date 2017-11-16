package com.wandm.models.playlist

object ListPlaylist {
    val instance = ListPlaylist

    val mList = ArrayList<String>()

    fun add(value: String) {
        mList.add(value)
    }

    fun get(position: Int) = mList[position]

    fun getList() = mList

    fun size() = mList.size

    fun remove(position: Int) {
        mList.removeAt(position)
    }

}