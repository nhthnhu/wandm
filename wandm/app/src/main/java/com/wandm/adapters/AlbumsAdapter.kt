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
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_album.view.*


class AlbumsAdapter(private val mListAlbums: ArrayList<Album>) : RecyclerView.Adapter<AlbumsAdapter.AlbumsHolder>(),
        BubbleTextGetter {

    override fun getTextToShowInBubble(pos: Int): String {
        if (mListAlbums.size == 0) return ""
        return mListAlbums[pos].title[0].toString()
    }

    private var onItemClickListener: ((album: Album, position: Int) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((album: Album, position: Int) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumsHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_album, parent, false)
        return AlbumsHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumsHolder, position: Int) {
        holder.bind(mListAlbums[position], position)
    }

    override fun getItemCount() = mListAlbums.size

    override fun onViewAttachedToWindow(holder: AlbumsHolder?) {
        super.onViewAttachedToWindow(holder)
    }

    inner class AlbumsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(album: Album, pos: Int) {
            setupSize(itemView)

            val countStr = itemView?.context?.resources?.
                    getQuantityString(R.plurals.song_count, album.songCount, album.songCount)

            itemView.albumName.text = album.title

            itemView.numbersongs.text = itemView?.context?.resources?.
                    getString(R.string.count, album.artistName, countStr)

            Picasso.with(itemView.context)
                    .load(Utils.getAlbumArtUri(album.id).toString())
                    .into(itemView.albumImage, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            itemView.albumImage.background = itemView.context.getDrawable(R.drawable.ic_action_music)
                        }
                    })

            itemView.albumName.isSelected = true
            itemView.numbersongs.isSelected = true

            itemView.setOnClickListener {
                onItemClickListener?.invoke(album, pos)
            }
        }
    }

    private fun setupSize(itemView: View) {
        val textSize = Utils.getTextSize()
        itemView.albumName.textSize = textSize.toFloat()
        itemView.numbersongs.textSize = (textSize - 4).toFloat()
    }

}