package com.wandm.fragments

import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.wandm.App
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SearchAdapter
import com.wandm.data.CurrentPlaylistManager
import com.wandm.models.RequestListener
import com.wandm.models.song.Song
import com.wandm.models.song.SongRequest
import com.wandm.services.MusicPlayer
import kotlinx.android.synthetic.main.dialog_search.*
import java.util.*


class SearchDialogFragment : BaseDialogFragment() {

    private val TAG = "SearchDialogFragment"
    private val REQ_CODE_SPEECH_INPUT = 100
    private var listSong: ArrayList<Song>? = null

    companion object {
        fun newInstance(): SearchDialogFragment {
            val fragment = SearchDialogFragment()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keywordEditText.setOnEditorActionListener({ v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val musicListener = object : RequestListener<ArrayList<Song>> {
                    override fun onStart() {
                        super.onStart()
                        songsProgressBar.visibility = View.VISIBLE
                    }

                    override fun onComplete(data: ArrayList<Song>?) {
                        if (data != null) {
                            songsProgressBar.visibility = View.GONE
                            listSong = data
                            val adapter = SearchAdapter(data) { song, position, state ->
                                when (state) {
                                    "Dowload" -> {
                                        Log.d(TAG, state)
                                    }

                                    "Play" -> {
                                        CurrentPlaylistManager.mListSongs = listSong!!
                                        CurrentPlaylistManager.mPosition = position
                                        MusicPlayer.bind(null)

                                        val intent = Intent(activity, NowPlayingActivity::class.java)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(intent)
                                        dialog.dismiss()
                                    }
                                }
                            }

                            listOnlineSong.layoutManager = LinearLayoutManager(App.instance)
                            listOnlineSong.adapter = adapter
                        }
                    }
                }
                SongRequest(keywordEditText.text.toString(), musicListener).execute()
                handled = true
            }
            handled
        })

        speechToTextButton.setOnClickListener {
            val fragmentManager = MainActivity.instance.supportFragmentManager
            val dialogFragment = SpeechDialogFragment.newInstance()
            dialogFragment.show(fragmentManager, "SpeechDialogFragment")
        }

    }
}