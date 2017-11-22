package com.wandm.adapters

import android.annotation.SuppressLint
import android.nfc.Tag
import android.support.v7.widget.RecyclerView
import android.util.Log
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

    private val TAG = "ArtistAlbumAdapter"

    override fun onBindViewHolder(holder: AlbumHolder?, position: Int) {
        holder?.bind(listAlbums[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_artist_album, parent, false)
        return AlbumHolder(view)
    }

    override fun getItemCount(): Int {
        return listAlbums.size
    }


    class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mAlbum: Album? = null

        @SuppressLint("SetTextI18n")
        fun bind(album: Album) {
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

            var string = ""
            if (album.songCount <= 1)
                string = itemView.context.getString(R.string.song)
            else
                string = itemView.context.getString(R.string.songs)

            itemView.numbersongsTextView.text = album.songCount.toString() + " " + string
        }

        fun getAlbumId(): Long? {
            return mAlbum?.id
        }
    }
}
