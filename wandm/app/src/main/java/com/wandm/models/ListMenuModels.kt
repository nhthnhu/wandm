package com.wandm.models

import com.wandm.R

class ListMenuModels private constructor() {
    private val mList = ArrayList<MenuModel>()

    companion object {
        val instance by lazy { ListMenuModels() }
    }

    init {
        val setting = MenuModel(R.drawable.ic_action_search_dark, "Setting", false)
        mList.add(setting)
    }

    operator fun get(position: Int): MenuModel {
        return mList[position]
    }

    fun size() = mList.size
}