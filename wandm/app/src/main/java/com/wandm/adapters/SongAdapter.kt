package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.models.Song
import com.wandm.services.MusicPlayer
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_song.view.*

class SongAdapter(private val listSongs: ArrayList<Song>) : RecyclerView.Adapter<SongAdapter.SongHolder>(), BubbleTextGetter {
    override fun getTextToShowInBubble(pos: Int): String {
        return listSongs[pos].title[0].toString()
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_song, parent, false)
        return SongHolder(view)
    }

    override fun onBindViewHolder(holder: SongHolder?, position: Int) {
        holder?.bind(listSongs[position])
        holder?.songItemView?.setOnClickListener {
            CurrentPlaylistManager.mListSongs = listSongs
            CurrentPlaylistManager.mPosition = position

            MusicPlayer.bind(null)
        }
    }


    class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songItemView: View = itemView

        fun bind(song: Song) {
            itemView.titleItemSongTextView.text = song.title
            itemView.artistItemSongTextView.text = song.artistName
        }
    }
}