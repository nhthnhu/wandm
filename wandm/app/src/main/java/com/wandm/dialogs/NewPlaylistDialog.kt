package com.wandm.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wandm.R
import com.wandm.database.MusicDBHandler
import com.wandm.database.PlaylistSongsTable
import com.wandm.database.PlaylistsTable
import com.wandm.models.Playlist
import com.wandm.models.song.Song
import kotlinx.android.synthetic.main.dialog_new_playlist.*

class NewPlaylistDialog : DialogFragment() {

    companion object {
        private val ARG_SONG = "arg_song"

        fun newInstance(song: Song?): NewPlaylistDialog {
            val arg = Bundle()
            arg.putSerializable(ARG_SONG, song)
            val fragment = NewPlaylistDialog()
            fragment.arguments = arg
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.dialog_new_playlist,
                container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setTitle(getString(R.string.new_playlist))

        createButton.setOnClickListener {
            var playlist: Playlist? = Playlist(playlistNameEdit.text.toString())
            var musicDBHandler = MusicDBHandler.getInstance(activity, PlaylistsTable.TABLE_NAME)
            musicDBHandler?.insert(playlist)
            playlist = musicDBHandler?.getLatestPlaylist()
            musicDBHandler = MusicDBHandler.getInstance(activity, PlaylistSongsTable.TABLE_NAME)
            val song = arguments.get(ARG_SONG) as Song
            song.playlistId = playlist?.id ?: 0
            musicDBHandler?.insert(song)

            Toast.makeText(activity
                    , activity.resources.getString(R.string.added_to_playlist
                    , playlist?.name), Toast.LENGTH_SHORT).show()

            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}
