package com.wandm.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.App
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.activities.NowPlayingActivity
import com.wandm.data.CurrentPlaylistManager
import com.wandm.models.song.Song
import com.wandm.services.MusicPlayer
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_song.view.*

class SongsAdapter(private val listSongs: ArrayList<Song>,
                   private val listener: (Song, Int, String) -> Unit) : RecyclerView.Adapter<SongsAdapter.SongHolder>(), BubbleTextGetter {

    companion object {
        val ACTION_PLAY = "action_play"
        val ACTION_ADD_PLAYLIST = "action_add_playlist"
    }

    override fun getTextToShowInBubble(pos: Int) = listSongs[pos].title[0].toString()

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
            listener(listSongs[position], position, ACTION_PLAY)
        }

        holder?.songItemView?.playlistButton?.setOnClickListener {
            listener(listSongs[position], position, ACTION_ADD_PLAYLIST)
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