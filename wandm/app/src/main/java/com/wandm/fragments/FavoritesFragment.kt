package com.wandm.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wandm.App
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.FavoritesAdapter
import com.wandm.database.FavoritesTable
import com.wandm.database.MusicDBHandler
import com.wandm.models.Song
import com.wandm.services.MusicPlayer
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_songs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavoritesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
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
        container.setOnRefreshListener(this)

        mAdapter = FavoritesAdapter(ArrayList())
        songsRecyclerView.adapter = mAdapter
        setItemDecoration()

        MusicDBHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)
                ?.setInsertEvent(object : MusicDBHandler.InsertEvent {
                    override fun onInsert(tableName: String) {
                        if (tableName == FavoritesTable.TABLE_NAME)
                            loadSongs()
                    }
                })


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
            mList = MusicDBHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.getFavorites()

            uiThread {
                try {
                    if (mList != null) {
                        container.isRefreshing = false
                        mAdapter?.listSongs = mList!!
                        songsRecyclerView.adapter.notifyDataSetChanged()

                        if (mList!!.size > 0)
                            songsFastScroller.visibility = View.VISIBLE
                    }

                    songsProgressBar.visibility = View.GONE
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }
        }


        mAdapter?.setOnItemClickListener { song, position, action ->
            when (action) {
                FavoritesAdapter.ACTION_PLAY -> {
                    MusicPlayer.bind(null)

                    val intent = Intent(activity, NowPlayingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }

                FavoritesAdapter.ACTION_REMOVE -> {
                    val isSuccessfull = MusicDBHandler.getInstance(activity, FavoritesTable.TABLE_NAME)?.
                            remove(mList!![position])!!

                    if (isSuccessfull) {
                        mList?.remove(song)
                        mAdapter?.notifyDataSetChanged()
                        Toast.makeText(activity, R.string.remove_from_favorites, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }
}

