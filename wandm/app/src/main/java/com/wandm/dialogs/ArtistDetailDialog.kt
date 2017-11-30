package com.wandm.dialogs

import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.ArtistAlbumAdapter
import com.wandm.adapters.SongsAdapter
import com.wandm.loaders.ArtistAlbumLoader
import com.wandm.loaders.ArtistSongLoader
import com.wandm.services.MusicPlayer
import com.wandm.utils.PreferencesUtils
import com.wandm.views.CustomLayoutManager
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.dialog_artist_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArtistDetailDialog() : BaseDialog() {
    private var artistId = 0L
    private val speedScroll = 3000
    private val handler = Handler()
    private var count = 0

    private var min = true
    private var max = false

    private var songsAdapter: SongsAdapter? = null
    private var albumsAdapter: ArtistAlbumAdapter? = null

    companion object {
        private val TAG = "ArtistDetailDialog"
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

    private val runnable = object : Runnable {
        override fun run() {

            if (count == 0) {
                min = true
                max = false
            } else if (count == albumsAdapter?.itemCount!! - 1) {
                min = false
                max = true
            }

            if (min && !max)
                count++
            else if (!min && max)
                count--


            if (albumsRecyclerView != null)
                albumsRecyclerView.smoothScrollToPosition(count)
            handler.postDelayed(this, speedScroll.toLong())
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_artist_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        albumsRecyclerView.layoutManager = CustomLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        setupView()
        songsFastScroller.setRecyclerView(songsRecyclerView)

        loadArtistDetail()

        homeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        albumsRecyclerView.stopScroll()
        handler.removeCallbacks(runnable)
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

                songsAdapter = SongsAdapter(songs, false) { song, position, action ->
                    when (action) {
                        SongsAdapter.ACTION_PLAY -> {
                            dismiss()
                            MusicPlayer.bind(null)

                            val intent = Intent(activity, NowPlayingActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            activity.startActivity(intent)
                        }
                    }
                }

                albumsAdapter = ArtistAlbumAdapter(artistAlbums)

                uiThread {
                    showView(ACTION_ARTIST_DETAIL)
                    songsRecyclerView.adapter = songsAdapter
                    albumsRecyclerView.adapter = albumsAdapter
                    albumsRecyclerView.isClickable = false
                    albumsRecyclerView.postDelayed(runnable, speedScroll.toLong())
                    setItemDecoration()
                }
            }
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

    private fun setupView() {
        if (PreferencesUtils.getLightTheme())
            homeButton.setColor(activity.resources.getColor(R.color.color_light_theme))
        else
            homeButton.setColor(activity.resources.getColor(R.color.color_dark_theme))
    }
}