package com.wandm.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.App
import com.wandm.R
import com.wandm.model.ListMenuModels
import com.wandm.model.MenuModel
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    override fun onBindViewHolder(holder: MenuHolder?, position: Int) {
        holder?.bind(ListMenuModels.instance[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_menu, parent, false)
        return MenuHolder(view)
    }

    override fun getItemCount(): Int {
        return ListMenuModels.instance.size()
    }


    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: MenuModel) {
            itemView.menuImageView.background = App.instance.getDrawable(data.icon)
            itemView.contentMenuTextView.text = data.content
        }
    }

}