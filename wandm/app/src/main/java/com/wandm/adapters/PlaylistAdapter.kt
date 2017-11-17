package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import kotlinx.android.synthetic.main.item_playlist.view.*

class PlaylistAdapter(val listPlaylists: ArrayList<String>,
                      val listener: (String) -> Unit) : RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {

    private val TAG = "PlaylistAdapter"

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolder?, position: Int) {
        holder?.bind(listPlaylists.get(position))
        holder?.itemView?.setOnClickListener {
            listener(listPlaylists.get(position))
        }
    }

    override fun getItemCount(): Int {
        return listPlaylists.size
    }


    inner class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            itemView.playlistTextView.text = name
        }
    }
}