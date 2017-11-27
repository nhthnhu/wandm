package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.R
import com.wandm.adapters.PlaylistAdapter
import com.wandm.database.FavoritesTable
import com.wandm.database.MusicDBHandler
import com.wandm.database.PlaylistsTable
import com.wandm.dialogs.PlaylistDetailDialog
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_playlists.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PlaylistsFragment : BaseFragment() {

    private var playlistsAdapter: PlaylistAdapter? = null

    override fun getLayoutResId() = R.layout.fragment_playlists

    override fun onCreatedView(savedInstanceState: Bundle?) {
        setupViews()
        loadPlaylists()
    }

    private fun setupViews() {
        playlistsRecyclerView.visibility = View.GONE
        playlistsFastScroller.visibility = View.GONE
        playlistsProgressBar.visibility = View.VISIBLE

        playlistsRecyclerView.layoutManager = LinearLayoutManager(activity)
        playlistsFastScroller.setRecyclerView(playlistsRecyclerView)
        playlistsAdapter = PlaylistAdapter(ArrayList()) { playlist, i ->
            val playlistDetailDialog = PlaylistDetailDialog.newInstance(playlist.id)
            playlistDetailDialog.show(fragmentManager, PlaylistDetailDialog::javaClass.name)
        }
        playlistsRecyclerView.adapter = playlistsAdapter
        MusicDBHandler.getInstance(activity, PlaylistsTable.TABLE_NAME)
                ?.setInsertEvent(object : MusicDBHandler.InsertEvent {
                    override fun onInsert(tableName: String) {
                        if (tableName != FavoritesTable.TABLE_NAME)
                            loadPlaylists()
                    }

                })
    }

    private fun loadPlaylists() {
        if (activity != null) {

            doAsync {
                val playlists = MusicDBHandler.getInstance(activity, PlaylistsTable.TABLE_NAME)?.getPlaylists()
                if (playlists != null)
                    uiThread {
                        playlistsAdapter?.listPlaylists = playlists
                        setItemDecoration()
                        playlistsAdapter?.notifyDataSetChanged()
                        if (playlists.size > 0) {
                            playlistsRecyclerView.visibility = View.VISIBLE
                            playlistsFastScroller.visibility = View.VISIBLE
                        }
                        playlistsProgressBar.visibility = View.GONE
                    }
            }
        }
    }

    private fun setItemDecoration() {
        playlistsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

}