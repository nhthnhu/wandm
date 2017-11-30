package com.wandm.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.wandm.App
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.database.FavoritesTable
import com.wandm.database.MusicDBHandler
import com.wandm.models.Song
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_favorite_song.view.*

class FavoritesAdapter(private val listSongs: ArrayList<Song>) : RecyclerView.Adapter<FavoritesAdapter.FavoriteHolder>(), BubbleTextGetter {

    companion object {
        val ACTION_REMOVE = "action_remove"
        val ACTION_PLAY = "action_play"
    }

    private var onItemClickListener: ((song: Song, position: Int, action: String) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((song: Song, position: Int, action: String) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getTextToShowInBubble(pos: Int): String {
        if (listSongs.size == 0)
            return ""

        return listSongs[pos].title[0].toString()
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FavoriteHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_favorite_song, parent, false)
        return FavoriteHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteHolder?, position: Int) {
        holder?.bind(listSongs[position], position)
    }


    inner class FavoriteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songItemView: View = itemView

        fun bind(song: Song, position: Int) {
            setupSize(itemView)
            ImageLoader.getInstance().displayImage(
                    Utils.getAlbumArtUri(song.albumId).toString(),
                    itemView.albumArt, DisplayImageOptions.Builder().cacheInMemory(true).
                    showImageOnFail(R.drawable.ic_action_music).
                    resetViewBeforeLoading(true).build())

            itemView.titleItemSongTextView.text = song.title
            itemView.artistItemSongTextView.text = song.artistName

            itemView.titleItemSongTextView.isSelected = true
            itemView.artistItemSongTextView.isSelected = true


            itemView.favoriteButton.setOnClickListener {
                onItemClickListener?.invoke(song, position, ACTION_REMOVE)
            }

            itemView.setOnClickListener {
                CurrentPlaylistManager.listSongs = listSongs
                CurrentPlaylistManager.position = position

                onItemClickListener?.invoke(song, position, ACTION_PLAY)
            }

            itemView.setOnLongClickListener {
                onItemClickListener?.invoke(song, position, ACTION_REMOVE)
                true
            }
        }

    }

    private fun removeSong(position: Int): Boolean {
        val isSuccessfull = MusicDBHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                remove(listSongs[position])!!

        if (isSuccessfull) {
            listSongs.remove(listSongs[position])
            this.notifyDataSetChanged()
            return true
        }
        return false
    }

    private fun setupSize(itemView: View) {
        val textSize = PreferencesUtils.getTextSize()
        itemView.titleItemSongTextView.textSize = textSize.toFloat()
        itemView.artistItemSongTextView.textSize = (textSize - 4).toFloat()
    }

}