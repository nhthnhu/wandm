package com.wandm.fragments

import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.adapters.PlaylistAdapter
import com.wandm.database.FavoritesTable
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.models.playlist.ListPlaylist
import kotlinx.android.synthetic.main.dialog_playlist.*
import org.greenrobot.eventbus.EventBus

class PlaylistDialogFragment : BaseDialogFragment() {

    private val TAG = "PlaylistDialogFragment"
    private var tableName = ""

    companion object {
        private var listener: ((String) -> Unit)? = null

        fun newInstance(listener: (String) -> Unit): PlaylistDialogFragment {
            this.listener = listener
            val fragment = PlaylistDialogFragment()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_playlist, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PlaylistAdapter(ListPlaylist.mList) { playlist ->
            listener!!(playlist)
            removeFragment()
        }

        listPlaylist.layoutManager = LinearLayoutManager(activity)
        listPlaylist.adapter = adapter

        favoriteButton.setOnClickListener {
            listener!!("favoriteSong")
            EventBus.getDefault().post(MessageEvent(MusicEvent.ADD_FAVORITE))
        }

        newPlaylistButton.setOnClickListener {
            newPlaylistLayout.visibility = View.VISIBLE
            addNewButton.setOnClickListener {
                val name = newPlaylistEditText.text.toString()
                if (true) {
                    listener!!(name)
                }
                removeFragment()
            }
        }

        favoriteButton.setOnClickListener {
            listener!!(FavoritesTable.TABLE_NAME)
            removeFragment()
        }

    }

    private fun removeFragment() {
        var fragment = fragmentManager.findFragmentByTag("PlaylistDialogFragment")
        fragment = fragment as PlaylistDialogFragment
        fragment.dismiss()
    }

}
