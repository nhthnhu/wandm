package com.wandm.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.adapters.PlaylistAdapter
import com.wandm.database.MusicDBHandler
import com.wandm.database.PlaylistSongsTable
import com.wandm.database.PlaylistsTable
import com.wandm.models.Song
import com.wandm.utils.PreferencesUtils
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.dialog_playlists.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread

class PlaylistsDialog : BaseDialog() {
    private var song: Song? = null
    private var playlistsAdapter: PlaylistAdapter? = null
    private var colorResId = R.color.color_dark_theme

    companion object {

        private val ARG_SONG = "arg_song"

        fun newInstance(song: Song): PlaylistsDialog {
            val arg = Bundle()
            arg.putSerializable(ARG_SONG, song)
            val fragment = PlaylistsDialog()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            fragment.arguments = arg
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_playlists,
                container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        newPlaylistLayout.setOnClickListener {
            val newPlaylistDialog = NewPlaylistDialog.newInstance(song)
            newPlaylistDialog.show(fragmentManager, NewPlaylistDialog::javaClass.name)
            dismiss()
        }

        homeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadPlaylists()
    }

    private fun setupViews() {
        if (PreferencesUtils.getLightTheme())
            colorResId = R.color.color_light_theme

        homeButton.setColor(activity.resources.getColor(colorResId))
        newPlaylistButton.setColor(activity.resources.getColor(colorResId))

        song = arguments.getSerializable(ARG_SONG) as Song
        playlistsAdapter = PlaylistAdapter(ArrayList()) { playlist, i, action ->

            song?.playlistId = playlist.id
            MusicDBHandler.getInstance(activity, PlaylistSongsTable.TABLE_NAME)?.insert(song)

            toast(activity.resources.getString(R.string.added_to_playlist, playlist.name))

            dismiss()
        }

        playlistsRecyclerView.adapter = playlistsAdapter
        playlistsRecyclerView.visibility = View.GONE
        playlistsFastScroller.visibility = View.GONE
        playlistsProgressBar.visibility = View.VISIBLE

        playlistsRecyclerView.layoutManager = LinearLayoutManager(activity)
        playlistsFastScroller.setRecyclerView(playlistsRecyclerView)
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