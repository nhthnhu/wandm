package com.wandm.fragments

import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.adapters.PlaylistAdapter
import com.wandm.database.FavoritesTable
import com.wandm.models.ListPlaylists
import kotlinx.android.synthetic.main.dialog_playlist.*

class PlaylistDialogFragment : BaseDialogFragment() {

    private var tableName = ""

    companion object {
        private var listener: ((String) -> Unit)? = null
        private val TABLE_NAME_EXTRA = "table_name_extra"

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
        val adapter = PlaylistAdapter(ListPlaylists.mList) { playlist ->
            listener!!(playlist)
            removeFragment()
        }

        listPlaylist.layoutManager = LinearLayoutManager(activity)
        listPlaylist.adapter = adapter

        favoriteButton.setOnClickListener {
            listener!!("favoriteSong")
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
