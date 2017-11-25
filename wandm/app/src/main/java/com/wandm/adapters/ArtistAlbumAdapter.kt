package com.wandm.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.models.Album
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.item_artist_album.view.*


class ArtistAlbumAdapter(private val listAlbums: ArrayList<Album>) : RecyclerView.Adapter<ArtistAlbumAdapter.AlbumHolder>() {


    override fun onBindViewHolder(holder: AlbumHolder?, position: Int) {
        holder?.bind(listAlbums[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_artist_album, parent, false)
        return AlbumHolder(view)
    }

    override fun getItemCount() = listAlbums.size

    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mAlbum: Album? = null

        @SuppressLint("SetTextI18n")
        fun bind(album: Album, position: Int) {
            mAlbum = album

            Picasso.with(itemView.context)
                    .load(Utils.getAlbumArtUri(album.id).toString())
                    .into(itemView.albumImageView, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            itemView.albumImageView.background = itemView.context.getDrawable(R.drawable.ic_music)
                        }
                    })

            itemView.albumNameTextView.text = album.title

            itemView.detailAlbumTextView.text = itemView?.resources?.
                    getQuantityString(R.plurals.song_count, album.songCount, album.songCount)

        }
    }
}
