package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.adapters.FavoritesAdapter
import com.wandm.data.CurrentPlaylistManager
import com.wandm.database.FavoritesTable
import com.wandm.database.SongsBaseHandler
import com.wandm.database.SongsBaseHelper
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.models.song.Song
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.content_now_playing.*
import kotlinx.android.synthetic.main.fragment_songs.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavoritesFragment : BaseFragment() {
    private val TAG = "FavoritesFragment"
    private var mList: ArrayList<Song>? = null

    private var mAdapter: FavoritesAdapter? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_songs
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z)
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        songsFastScroller.setRecyclerView(songsRecyclerView)

        SongsBaseHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)
                ?.setAddSongEvent(object : SongsBaseHandler.AddSongEvent {
                    override fun onAddSong() {
                        updateList()
                    }
                })

        if (activity != null) {
            updateList()
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

    private fun updateList() {
        doAsync {
            mList = SongsBaseHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.getList()
            if (mList != null) {
                mAdapter = FavoritesAdapter(mList!!)
                uiThread {
                    songsRecyclerView.adapter = mAdapter
                    setItemDecoration()
                    songsRecyclerView.adapter.notifyDataSetChanged()
                    songsFastScroller.visibility = View.VISIBLE
                    songsProgressBar.visibility = View.GONE
                }
            }

        }
    }
}
