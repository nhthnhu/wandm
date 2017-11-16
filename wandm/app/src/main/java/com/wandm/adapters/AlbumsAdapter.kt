package com.wandm.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.models.Album
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_album.view.*


class AlbumsAdapter(private val mListAlbums: ArrayList<Album>) : RecyclerView.Adapter<AlbumsAdapter.AlbumsHolder>(), BubbleTextGetter {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumsHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_album, parent, false)
        return AlbumsHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumsHolder, position: Int) {
        holder.bind(mListAlbums[position])
    }

    override fun getItemCount() = mListAlbums.size

    override fun getTextToShowInBubble(pos: Int) = mListAlbums[pos].title[0].toString()

    inner class AlbumsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(album: Album) {
            itemView.albumName.text = album.title
            itemView.numbersongs.text = album.artistName + " | " + album.songCount + itemView.context.getString(R.string.songs)
        }
    }

}