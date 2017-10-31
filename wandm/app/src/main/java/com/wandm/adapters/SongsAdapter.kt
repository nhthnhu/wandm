package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
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

            ImageLoader.getInstance().displayImage(
                    Utils.getAlbumArtUri(song.albumId).toString(),
                    itemView.albumArt, DisplayImageOptions.Builder().cacheInMemory(true).
                    showImageOnFail(R.drawable.ic_action_headset_dark).
                    resetViewBeforeLoading(true).build())

            itemView.titleItemSongTextView.text = song.title
            itemView.artistItemSongTextView.text = song.artistName

            itemView.titleItemSongTextView.isSelected = true
            itemView.artistItemSongTextView.isSelected = true
        }

    }

}