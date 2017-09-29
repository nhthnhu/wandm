package com.wandm.models

import com.wandm.R

class ListMenus private constructor() {
    private val mList = ArrayList<Menu>()

    companion object {
        val instance by lazy { ListMenus() }
    }

    init {
        val setting = Menu(R.drawable.ic_action_search_dark, "Setting", false)
        mList.add(setting)
    }

    operator fun get(position: Int): Menu {
        return mList[position]
    }

    fun size() = mList.size
}