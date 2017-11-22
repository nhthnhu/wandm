package com.wandm.dialogs

import android.app.DialogFragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.ArtistAlbumAdapter
import com.wandm.adapters.SongsAdapter
import com.wandm.loaders.AlbumSongLoader
import com.wandm.loaders.ArtistAlbumLoader
import com.wandm.loaders.ArtistSongLoader
import com.wandm.services.MusicPlayer
import com.wandm.utils.Utils
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.dialog_artist_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArtistDetailDialog : BaseDialogFragment(), View.OnClickListener {
    private var artistId = 0L
    private val TAG = "ArtistDetailDialog"

    companion object {
        private val ARG_ARTIST_ID = "arg_artist_id"

        private val ACTION_LOADING = "action_loading"
        private val ACTION_ARTIST_DETAIL = "action_artist_detail"

        fun newInstance(artistId: Long): ArtistDetailDialog {
            val bundle = Bundle()
            bundle.putLong(ARG_ARTIST_ID, artistId)

            val fragment = ArtistDetailDialog()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_artist_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        albumsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        songsFastScroller.setRecyclerView(songsRecyclerView)

        loadArtistDetail()
    }

    override fun onClick(p0: View?) {

    }

    private fun showView(action: String) {
        when (action) {
            ACTION_LOADING -> {
                songsProgressBar.visibility = View.VISIBLE
                songsFastScroller.visibility = View.VISIBLE
            }

            ACTION_ARTIST_DETAIL -> {
                songsFastScroller.visibility = View.VISIBLE
                songsProgressBar.visibility = View.GONE
            }
        }
    }

    private fun getArtistId(): Long = arguments.getLong(ARG_ARTIST_ID, 0)

    private fun loadArtistDetail() {
        artistId = getArtistId()
        showView(ACTION_LOADING)

        if (activity != null) {
            doAsync {
                val artistAlbums = ArtistAlbumLoader.getAlbumsForArtist(activity, artistId)
                val songs = ArtistSongLoader.getSongsForArtist(activity, artistId)

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

                val albumsAdapter = ArtistAlbumAdapter(artistAlbums)

                uiThread {
                    showView(ACTION_ARTIST_DETAIL)
                    songsRecyclerView.adapter = songsAdapter
                    albumsRecyclerView.adapter = albumsAdapter
                    setItemDecoration()
                }
            }
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }
}