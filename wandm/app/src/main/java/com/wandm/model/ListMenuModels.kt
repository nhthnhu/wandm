package com.wandm.model

import com.wandm.R

class ListMenuModels private constructor() {
    private val mList = ArrayList<MenuModel>()

    companion object {
        val instance by lazy { ListMenuModels() }
    }

    init {
        val setting = MenuModel(R.drawable.ic_action_search, "Setting", false)
        mList.add(setting)
    }

    operator fun get(position: Int): MenuModel {
        return mList[position]
    }

    fun size() = mList.size
}