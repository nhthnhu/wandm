package com.wandm.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SongsAdapter
import com.wandm.data.SearchDataHelper
import com.wandm.database.SongsBaseHandler
import com.wandm.dialogs.PlaylistDialogFragment
import com.wandm.loaders.SongLoader
import com.wandm.services.MusicPlayer
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
                val songs = SongLoader.getAllSongs(App.instance)
                SearchDataHelper.setSongSuggestions(songs)

                adapter = SongsAdapter(songs, true) { song, position, action ->
                    when (action) {
                        SongsAdapter.ACTION_ADD_PLAYLIST -> {
                            val fragmentManager = MainActivity.instance.supportFragmentManager
                            val dialogFragment = PlaylistDialogFragment.newInstance { title ->
                                SongsBaseHandler.getInstance(App.instance, title)?.addSong(song)
                            }
                            dialogFragment.show(fragmentManager, "PlaylistDialogFragment")
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
                    songsRecyclerView.adapter = adapter
                    setItemDecoration()
                    songsRecyclerView.adapter.notifyDataSetChanged()

                    if (songs.size > 0)
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