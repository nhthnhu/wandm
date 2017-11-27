package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.models.playlist.Playlist
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_artist.view.*
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
    private var colorResId = R.color.color_dark_theme

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


    inner class PlaylistHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(playlist: Playlist) {
            setupView(itemView)
            
            if (PreferencesUtils.getLightTheme()) {
                itemView.playlistsIcon.setColor(itemView.resources.getColor(R.color.color_light_theme))
            } else {
                itemView.playlistsIcon.setColor(itemView.resources.getColor(R.color.color_dark_theme))
            }


            itemView.playlistNameView.text = playlist.name
            itemView.playlistSongCount.text = itemView?.context?.resources?.
                    getQuantityString(R.plurals.song_count, playlist.songCount, playlist.songCount)
        }
    }

    private fun setupView(itemView: View) {
        val textSize = PreferencesUtils.getTextSize()
        itemView.playlistNameView.textSize = textSize.toFloat()
        itemView.playlistSongCount.textSize = (textSize - 4).toFloat()

        if (PreferencesUtils.getLightTheme())
            colorResId = R.color.color_light_theme

        itemView.playlistsIcon.setColor(itemView.resources?.getColor(colorResId)!!)
    }
}