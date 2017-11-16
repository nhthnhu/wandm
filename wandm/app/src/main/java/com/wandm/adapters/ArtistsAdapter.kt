package com.wandm.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.models.Artist
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_artist.view.*

class ArtistsAdapter(private val mListArtists: ArrayList<Artist>) : RecyclerView.Adapter<ArtistsAdapter.ArtistsHolder>(), BubbleTextGetter {
    override fun getTextToShowInBubble(pos: Int) = mListArtists[pos].name[0].toString()

    override fun onBindViewHolder(holder: ArtistsHolder?, position: Int) {
        holder?.bind(mListArtists[position])
    }

    override fun getItemCount(): Int {
        return mListArtists.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArtistsHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_artist, parent, false)
        return ArtistsHolder(view)
    }


    inner class ArtistsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(artist: Artist) {
            itemView.artistName.text = artist.name
            val albumCount = artist.albumCount
            val songCount = artist.songCount

            val albumString: String
            val songString: String

            if (albumCount <= 1)
                albumString = albumCount.toString() + " album"
            else
                albumString = albumCount.toString() + " albums"

            if (songCount <= 1)
                songString = songCount.toString() + " song"
            else
                songString = songCount.toString() + " songs"

            itemView.albumSongsSount.text = albumString + " | " + songString
        }
    }
}