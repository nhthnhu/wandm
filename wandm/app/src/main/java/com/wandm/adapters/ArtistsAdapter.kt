package com.wandm.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.loaders.ArtistAlbumLoader
import com.wandm.models.Artist
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_artist.view.*

class ArtistsAdapter(private val mListArtists: ArrayList<Artist>) : RecyclerView.Adapter<ArtistsAdapter.ArtistsHolder>(),
        BubbleTextGetter {

    override fun getTextToShowInBubble(pos: Int) = mListArtists[pos].name[0].toString()

    private var onItemClickListener: ((artist: Artist, position: Int) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((artist: Artist, position: Int) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: ArtistsHolder?, position: Int) {
        holder?.bind(mListArtists[position], position)
    }

    override fun getItemCount() = mListArtists.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArtistsHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_artist, parent, false)
        return ArtistsHolder(view)
    }


    inner class ArtistsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(artist: Artist, pos: Int) {
            itemView.artistName.text = artist.name
            val albumCount = artist.albumCount
            val songCount = artist.songCount

            val albumString: String
            val songString: String

            if (albumCount <= 1)
                albumString = albumCount.toString() + " " +
                        itemView.context.resources.getString(R.string.album)
            else
                albumString = albumCount.toString() + " " +
                        itemView.context.resources.getString(R.string.albums)

            if (songCount <= 1)
                songString = songCount.toString() + " " +
                        itemView.context.resources.getString(R.string.song)
            else
                songString = songCount.toString() + " " +
                        itemView.context.resources.getString(R.string.songs)

            itemView.albumSongsSount.text = albumString + " | " + songString

            val albums = ArtistAlbumLoader.getAlbumsForArtist(itemView.context, artist.id)

            Picasso.with(itemView.context)
                    .load(Utils.getAlbumArtUri(albums[0].id).toString())
                    .into(itemView.artistImage, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            itemView.artistImage.background = itemView.context.getDrawable(R.drawable.ic_action_music)
                        }
                    })

            itemView.setOnClickListener {
                onItemClickListener?.invoke(artist, pos)
            }
        }
    }
}