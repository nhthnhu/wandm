package com.wandm.dialogs

import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SongsAdapter
import com.wandm.models.MusicFolder
import com.wandm.services.MusicPlayer
import com.wandm.utils.PreferencesUtils
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.dialog_song_folder_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SongFolderDetailDialog : BaseDialog() {
    private var musicFolder: MusicFolder? = null
    private var songsAdapter: SongsAdapter? = null

    companion object {
        private val ARG_FILE = "arg_file"

        fun newInstance(musicFolder: MusicFolder): SongFolderDetailDialog {
            val arg = Bundle()
            arg.putSerializable(ARG_FILE, musicFolder)

            val fragment = SongFolderDetailDialog()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            fragment.arguments = arg
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        musicFolder = arguments.getSerializable(ARG_FILE) as MusicFolder
        val view = inflater?.inflate(R.layout.dialog_song_folder_detail, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadSongs()

        homeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupViews() {
        songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        songsFastScroller.setRecyclerView(songsRecyclerView)

        if (PreferencesUtils.getLightTheme())
            homeButton.setColor(activity.resources.getColor(R.color.color_light_theme))
        else
            homeButton.setColor(activity.resources.getColor(R.color.color_dark_theme))
    }

    private fun loadSongs() {
        songsProgressBar.visibility = View.VISIBLE
        if (musicFolder == null) dismiss()

        doAsync {
            val songs = musicFolder?.songs

            if (songs == null) {
                dismiss()
                return@doAsync
            }

            songsAdapter = SongsAdapter(songs, true) { song, position, action ->
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

            uiThread {
                songsRecyclerView.adapter = songsAdapter
                setItemDecoration()
                songsRecyclerView.adapter.notifyDataSetChanged()

                if (songs.size > 0)
                    songsFastScroller.visibility = View.VISIBLE

                songsProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setItemDecoration() {
        songsRecyclerView.addItemDecoration(DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL_LIST))
    }
}
