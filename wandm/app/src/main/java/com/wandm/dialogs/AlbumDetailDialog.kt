package com.wandm.dialogs

import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SongsAdapter
import com.wandm.loaders.AlbumLoader
import com.wandm.loaders.AlbumSongLoader
import com.wandm.services.MusicPlayer
import com.wandm.utils.Utils
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.dialog_album_detail.*
import kotlinx.android.synthetic.main.dialog_album_detail.view.*
import kotlinx.android.synthetic.main.item_album.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AlbumDetailDialog : BaseDialog() {
    private var albumId = 0L
    private val TAG = "AlbumDetailDialog"

    companion object {
        private val ARG_ALBUM_ID = "arg_album_id"

        private val ACTION_LOADING = "action_loading"
        private val ACTION_ALBUM_DETAIL = "action_album_detail"

        fun newInstance(albumId: Long): AlbumDetailDialog {
            val bundle = Bundle()
            bundle.putLong(ARG_ALBUM_ID, albumId)

            val fragment = AlbumDetailDialog()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_album_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        songsFastScroller.setRecyclerView(songsRecyclerView)

        setupSize()

        loadAlbumDetail()
    }

    private fun showView(action: String) {
        when (action) {
            ACTION_LOADING -> {
                songsProgressBar.visibility = View.VISIBLE
                songsFastScroller.visibility = View.VISIBLE
            }

            ACTION_ALBUM_DETAIL -> {
                songsFastScroller.visibility = View.VISIBLE
                songsProgressBar.visibility = View.GONE
            }
        }
    }

    private fun getAlbumId(): Long = arguments.getLong(ARG_ALBUM_ID, 0)

    private fun loadAlbumDetail() {
        albumId = getAlbumId()
        showView(ACTION_LOADING)

        if (activity != null) {
            doAsync {
                val songs = AlbumSongLoader.getSongsForAlbum(activity, albumId)

                val songsAdapter = SongsAdapter(songs, false) { song, position, action ->
                    when (action) {
                        SongsAdapter.ACTION_PLAY -> {
                            dismiss()
                            MusicPlayer.bind(null)

                            val intent = Intent(activity, NowPlayingActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            activity.startActivity(intent)
                        }
                    }
                }

                uiThread {
                    val album = AlbumLoader.getAlbum(activity, albumId)
                    val numberSong = album.songCount
                    var song = ""
                    if (numberSong <= 1)
                        song = resources.getString(R.string.song)
                    else
                        song = resources.getString(R.string.songs)

                    albumNameTextView.text = album.title
                    detailAlbumTextView.text = album.artistName + " | " + album.songCount.toString() + " " + song

                    Picasso.with(activity)
                            .load(Utils.getAlbumArtUri(album.id).toString())
                            .into(albumImageView, object : Callback {
                                override fun onSuccess() {

                                }

                                override fun onError() {
                                    albumImageView.background = activity.getDrawable(R.drawable.ic_action_music)
                                }
                            })

                    showView(ACTION_ALBUM_DETAIL)
                    songsRecyclerView.adapter = songsAdapter
                    setItemDecoration()
                }
            }
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

    private fun setupSize() {
        val textSize = Utils.getTextSize()
        albumNameTextView.textSize = textSize.toFloat()
        detailAlbumTextView.textSize = (textSize - 4).toFloat()
    }

}