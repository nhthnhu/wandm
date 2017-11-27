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
import com.wandm.utils.PreferencesUtils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_online_song.view.*

class OnlineSongsAdapter(var listSongs: ArrayList<Song>,
                         private val listener: (Song, Int, String) -> Unit) : RecyclerView.Adapter<OnlineSongsAdapter.SearchHolder>(), BubbleTextGetter {

    private var colorResId = R.color.color_dark_theme

    companion object {
        val ACTION_PLAY = "action_play"
        val ACTION_DOWNLOAD = "action_download"
    }

    override fun getTextToShowInBubble(pos: Int): String {
        return listSongs[pos].title[0].toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_online_song, parent, false)
        return SearchHolder(view)
    }

    override fun getItemCount() = listSongs.size

    override fun onBindViewHolder(holder: SearchHolder?, position: Int) {
        holder?.bind(listSongs[position])

        holder?.itemView?.titleItemSongTextView?.setOnClickListener {
            CurrentPlaylistManager.listSongs = listSongs
            listener(listSongs[position], position, ACTION_PLAY)
        }

        holder?.itemView?.downloadtButton?.setOnClickListener {
            listener(listSongs[position], position, ACTION_DOWNLOAD)
        }
    }


    inner class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {
            setupView(itemView)
            itemView.titleItemSongTextView.text = song.title
            itemView.titleItemSongTextView.isSelected = true

            Picasso.with(itemView.context).load(song.albumArt).into(itemView.albumArt, object : Callback {
                override fun onSuccess() {

                }

                override fun onError() {
                    itemView.albumArt.background = itemView.context.getDrawable(R.drawable.ic_action_music)
                }
            })
        }

    }

    private fun setupView(itemView: View) {
        if (PreferencesUtils.getLightTheme())
            colorResId = R.color.color_light_theme

        val textSize = PreferencesUtils.getTextSize()
        itemView.titleItemSongTextView.textSize = textSize.toFloat()
        itemView.downloadtButton.setColor(itemView.resources?.getColor(colorResId)!!)
    }
}