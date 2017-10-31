package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.models.song.Song
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_online_song.view.*

class SearchAdapter(private val listSongs: ArrayList<Song>,
                    val listener: (Song, Int, String) -> Unit) : RecyclerView.Adapter<SearchAdapter.SearchHolder>(), BubbleTextGetter {

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
            listener(listSongs.get(position), position, "Play")
        }

        holder?.itemView?.downloadtButton?.setOnClickListener {
            listener(listSongs.get(position), position,"Download")
        }
    }


    inner class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {
            itemView.titleItemSongTextView.text = song.title
            itemView.titleItemSongTextView.isSelected = true
            Picasso.with(itemView.context).load(song.albumArt).into(itemView.albumArt)
        }

    }
}