package com.wandm.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SongsAdapter
import com.wandm.data.SearchHelper
import com.wandm.loaders.SongLoader
import com.wandm.services.MusicPlayer
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_songs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class SongsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {


    private var adapter: SongsAdapter? = null
    private val TAG = "SongsFragment"

    companion object {
        fun newInstance(): SongsFragment {
            return SongsFragment()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_songs
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z)
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        songsFastScroller.setRecyclerView(songsRecyclerView)
        container.setOnRefreshListener(this)

        if (activity != null) {
            loadSongs()
        }
    }

    override fun onRefresh() {
        if (activity != null) {
            container.isRefreshing = true
            loadSongs()
        }
    }

    private fun loadSongs() {
        doAsync {
            val songs = SongLoader.getAllSongs(App.instance)
            SearchHelper.setSongSuggestions(songs)

            adapter = SongsAdapter(songs, true) { song, position, action ->
                when (action) {
                    SongsAdapter.ACTION_ADD_PLAYLIST -> {

                    }

                    SongsAdapter.ACTION_PLAY -> {
                        MusicPlayer.bind(null)

                        val intent = Intent(activity, NowPlayingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        activity.startActivity(intent)
                    }
                }
            }

            uiThread {
                container.isRefreshing = false

                songsRecyclerView.adapter = adapter
                setItemDecoration()
                songsRecyclerView.adapter.notifyDataSetChanged()

                if (songs.size > 0)
                    songsFastScroller.visibility = View.VISIBLE

                songsProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }
}