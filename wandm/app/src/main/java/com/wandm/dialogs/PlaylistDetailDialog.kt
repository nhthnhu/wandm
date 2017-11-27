package com.wandm.dialogs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SongsAdapter
import com.wandm.database.MusicDBHandler
import com.wandm.database.PlaylistSongsTable
import com.wandm.services.MusicPlayer
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.dialog_playlist_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PlaylistDetailDialog : BaseDialog() {
    private var playlistId: Int = 0
    private var songsAdapter: SongsAdapter? = null

    companion object {
        private val ARG_PLAYLIST_ID = "arg_file"

        fun newInstance(playlistId: Int): PlaylistDetailDialog {
            val arg = Bundle()
            arg.putInt(ARG_PLAYLIST_ID, playlistId)

            val fragment = PlaylistDetailDialog()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            fragment.arguments = arg
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_playlist_detail, container, false)
        playlistId = arguments.getInt(ARG_PLAYLIST_ID)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadSongs()
    }

    private fun setupViews() {
        playlistSongsRecyclerView.layoutManager = LinearLayoutManager(activity)
        playlistSongsFastScroller.setRecyclerView(playlistSongsRecyclerView)
        playlistSongsFastScroller.visibility = View.GONE
        playlistSongsRecyclerView.visibility = View.GONE
        playlistSongsProgressBar.visibility = View.VISIBLE

        songsAdapter = SongsAdapter(ArrayList(), false) { song, position, action ->
            when (action) {
                SongsAdapter.ACTION_ADD_PLAYLIST -> {

                }

                SongsAdapter.ACTION_PLAY -> {
                    MusicPlayer.bind(null)

                    val intent = Intent(activity, NowPlayingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    activity.startActivity(intent)

                    dismiss()
                }
            }
        }
        playlistSongsRecyclerView.adapter = songsAdapter
    }

    private fun loadSongs() {

        if (activity != null) {
            doAsync {
                val songs = MusicDBHandler.getInstance(activity, PlaylistSongsTable.TABLE_NAME)
                        ?.getPlaylistSongs(playlistId)

                if (songs != null) {
                    uiThread {
                        setItemDecoration()
                        songsAdapter?.listSongs = songs
                        songsAdapter?.notifyDataSetChanged()

                        if (songs.size > 0) {
                            playlistSongsFastScroller.visibility = View.VISIBLE
                            playlistSongsRecyclerView.visibility = View.VISIBLE
                        }

                        playlistSongsProgressBar.visibility = View.GONE
                    }
                }

                uiThread { playlistSongsProgressBar.visibility = View.GONE }
            }
        }
    }

    private fun setItemDecoration() {
        playlistSongsRecyclerView.addItemDecoration(DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL_LIST))
    }
}