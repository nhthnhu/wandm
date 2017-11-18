package com.wandm.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.wandm.App
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.data.CurrentPlaylistManager
import com.wandm.database.FavoritesTable
import com.wandm.database.SongsBaseHandler
import com.wandm.models.song.Song
import com.wandm.services.MusicPlayer
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_favorite_song.view.*

class FavoritesAdapter(private val listSongs: ArrayList<Song>) : RecyclerView.Adapter<FavoritesAdapter.FavoriteHolder>(), BubbleTextGetter {

    override fun getTextToShowInBubble(pos: Int): String {
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
        holder?.bind(listSongs[position])

        holder?.songItemView?.setOnClickListener {
            CurrentPlaylistManager.mListSongs = listSongs
            CurrentPlaylistManager.mPosition = position
            MusicPlayer.bind(null)


            val intent = Intent(App.instance, NowPlayingActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            App.instance.startActivity(intent)
        }

        holder?.songItemView?.setOnLongClickListener {
            removeSong(position)
        }

        holder?.songItemView?.favoriteButton?.setOnClickListener {
            removeSong(position)
        }
    }


    class FavoriteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    private fun removeSong(position: Int): Boolean {
        val isSuccessfull = SongsBaseHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                removeSong(listSongs[position])!!
        if (isSuccessfull) {
            listSongs.remove(listSongs[position])
            this.notifyDataSetChanged()
            return true
        }
        return false
    }

}