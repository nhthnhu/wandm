package com.wandm.fragments

import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import com.wandm.App
import com.wandm.R
import com.wandm.adapters.SearchAdapter
import com.wandm.models.RequestListener
import com.wandm.models.song.Song
import com.wandm.models.song.SongRequest
import kotlinx.android.synthetic.main.dialog_search.*



class SearchDialogFragment : BaseDialogFragment() {

    private val TAG = "SearchDialogFragment"
    private var listSong: ArrayList<Song>? = null

    companion object {
        private var listener: ((Song) -> Unit)? = null

        fun newInstance(listener: (Song) -> Unit): SearchDialogFragment {
            this.listener = listener
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

        keywordEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
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
                            val adapter = SearchAdapter(data) { song ->
                                Log.d(TAG, "Download")
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

    }

}