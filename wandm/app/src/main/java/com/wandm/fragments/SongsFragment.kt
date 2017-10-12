package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.adapters.SongsAdapter
import com.wandm.database.SongsBaseHandler
import com.wandm.loaders.SongLoader
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_songs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class SongsFragment : BaseFragment() {
    private var adapter: SongsAdapter? = null
    override fun getLayoutResId(): Int {
        return R.layout.fragment_songs
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z)
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        songsFastScroller.setRecyclerView(songsRecyclerView)

        if (activity != null) {
            doAsync {
                adapter = SongsAdapter(SongLoader.getAllSongs(App.instance)) { song, position ->
                    val fragmentManager = MainActivity.instance.supportFragmentManager
                    val dialogFragment = PlaylistDialogFragment.newInstance { title ->
                        SongsBaseHandler.getInstance(App.instance, title)?.addSong(song)
                    }
                    dialogFragment.show(fragmentManager, "PlaylistDialogFragment")
                }

                uiThread {
                    songsRecyclerView.adapter = adapter
                    setItemDecoration()
                    songsRecyclerView.adapter.notifyDataSetChanged()
                    songsFastScroller.visibility = View.VISIBLE
                    songsProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }
}