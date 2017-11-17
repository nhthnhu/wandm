package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.R
import com.wandm.adapters.AlbumsAdapter
import com.wandm.adapters.SongsAdapter
import com.wandm.loaders.ArtistAlbumLoader
import com.wandm.loaders.ArtistSongLoader
import kotlinx.android.synthetic.main.fragment_artist_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArtistDetailFragment : BaseFragment() {
    private var artistId = 0L

    companion object {
        private val ARG_ARTIST_ID = "arg_artist_id"

        private val ACTION_LOADING = "action_loading"
        private val ACTION_ARTIST_DETAIL = "action_artist_detail"

        fun newInstance(artistId: Long): ArtistDetailFragment {
            val bundle = Bundle()
            bundle.putLong(ARG_ARTIST_ID, artistId)

            val fragment = ArtistDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_artist_detail

    override fun onCreatedView(savedInstanceState: Bundle?) {
        artistId = getArtistId()
        loadArtistDetail()
    }

    private fun showView(action: String) {
        when (action) {
            ACTION_LOADING -> {
                artistLoading.visibility = View.VISIBLE
                artistDetailLayout.visibility = View.GONE
            }

            ACTION_ARTIST_DETAIL -> {
                artistLoading.visibility = View.GONE
                artistDetailLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun getArtistId(): Long = arguments.getLong(ARG_ARTIST_ID, 0)

    private fun loadArtistDetail() {
        showView(ACTION_LOADING)

        doAsync {
            val artistAlbums = ArtistAlbumLoader.getAlbumsForArtist(activity, artistId)
            val artistSongs = ArtistSongLoader.getSongsForArtist(activity, artistId)
            uiThread {
                albumsList.layoutManager = LinearLayoutManager(activity)
                songsList.layoutManager = LinearLayoutManager(activity,
                        LinearLayoutManager.HORIZONTAL, false)

                val albumsAdapter = AlbumsAdapter(artistAlbums)
                val songsAdapter = SongsAdapter(artistSongs) { _, _ -> }

                albumsList.adapter = albumsAdapter
                songsList.adapter = songsAdapter

                showView(ACTION_ARTIST_DETAIL)
            }
        }
    }
}