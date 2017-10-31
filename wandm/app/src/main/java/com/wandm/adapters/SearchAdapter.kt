package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.models.song.Song
import com.wandm.services.MusicPlayer
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_online_song.view.*

class SearchAdapter(private val listSongs: ArrayList<Song>,
                    val listener: (Song) -> Unit) : RecyclerView.Adapter<SearchAdapter.SearchHolder>(), BubbleTextGetter {

    override fun getTextToShowInBubble(pos: Int): String {
        return listSongs[pos].title[0].toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_online_song, parent, false)
        return SearchHolder(view)
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }

    override fun onBindViewHolder(holder: SearchHolder?, position: Int) {
        holder?.bind(listSongs[position])

        holder?.itemView?.titleItemSongTextView?.setOnClickListener {
            CurrentPlaylistManager.mListSongs = listSongs
            CurrentPlaylistManager.mPosition = position
            MusicPlayer.bind(null)
        }

        holder?.itemView?.downloadtButton?.setOnClickListener {
            listener(listSongs.get(position))
        }
    }


    inner class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {
            itemView.titleItemSongTextView.text = song.title
            itemView.titleItemSongTextView.isSelected = true
            itemView.albumArt.setImageBitmap(song.albumArt)
        }

    }
}