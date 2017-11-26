package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.models.playlist.Playlist
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_playlist.view.*

class PlaylistAdapter(var listPlaylists: ArrayList<Playlist>,
                      val listener: (Playlist, Int) -> Unit) : RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>(), BubbleTextGetter {
    override fun getTextToShowInBubble(pos: Int): String {
        if (listPlaylists.size > 0) {
            return listPlaylists[pos].name[0].toString()
        }

        return ""
    }

    private val TAG = "PlaylistAdapter"

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolder?, position: Int) {
        holder?.bind(listPlaylists.get(position))
        holder?.itemView?.setOnClickListener {
            listener(listPlaylists.get(position), position)
        }
    }

    override fun getItemCount(): Int {
        return listPlaylists.size
    }


    class PlaylistHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(playlist: Playlist) {
            itemView.playlistNameView.text = playlist.name
            itemView.playlistSongCount.text = itemView?.context?.resources?.
                    getQuantityString(R.plurals.song_count, playlist.songCount, playlist.songCount)
        }
    }
}