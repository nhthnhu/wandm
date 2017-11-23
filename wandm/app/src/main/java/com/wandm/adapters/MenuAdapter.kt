package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.models.Artist
import com.wandm.models.menu.ListMenus
import com.wandm.models.menu.Menu
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    private var onItemClickListener: ((position: Int) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((position: Int) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: MenuHolder?, position: Int) {
        holder?.bind(ListMenus.instance[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_menu, parent, false)
        return MenuHolder(view)
    }

    override fun getItemCount() = ListMenus.instance.size()


    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Menu, position: Int) {
            itemView.menuImageView.setIcon(data.icon)
            itemView.menuImageView.setColorResource(data.color)
            itemView.contentMenuTextView.text = data.content

            itemView?.setOnClickListener {
                onItemClickListener?.invoke(position)
            }
        }
    }

}