package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.models.song.Song
import com.wandm.services.MusicPlayer
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_song.view.*

class SongsAdapter(private val listSongs: ArrayList<Song>,
                   val listener: (Song, Int) -> Unit) : RecyclerView.Adapter<SongsAdapter.SongHolder>(), BubbleTextGetter {

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

        holder?.songItemView?.playlistButton?.setOnClickListener {
            listener(listSongs[position], position)
        }
    }


    class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songItemView: View = itemView

        fun bind(song: Song) {

            Picasso.with(itemView.context)
                    .load(Utils.getAlbumArtUri(song.albumId).toString())
                    .into(itemView.albumArt, object : Callback {
                override fun onSuccess() {

                }

                override fun onError() {
                   itemView.albumArt.background = itemView.context.getDrawable(R.drawable.ic_action_headset_dark)
                }
            })

            itemView.titleItemSongTextView.text = song.title
            itemView.artistItemSongTextView.text = song.artistName

            itemView.titleItemSongTextView.isSelected = true
            itemView.artistItemSongTextView.isSelected = true
        }

    }

}