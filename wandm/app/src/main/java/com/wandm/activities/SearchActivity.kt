package com.wandm.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.wandm.R
import com.wandm.adapters.OnlineSongsAdapter
import com.wandm.adapters.SongsAdapter
import com.wandm.data.CurrentPlaylistManager
import com.wandm.data.SearchDataHelper
import com.wandm.loaders.SongLoader
import com.wandm.models.RequestListener
import com.wandm.models.SongSearchSuggestion
import com.wandm.models.song.Song
import com.wandm.models.song.SongRequest
import com.wandm.services.MusicPlayer
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.doAsync

class SearchActivity : BaseActivity() {

    private var songsAdapter: SongsAdapter? = null
    private var onlineSongsAdapter: OnlineSongsAdapter? = null

    companion object {
        private val TAG = "SearchActivity"
        private val FIND_SUGGESTION_SIMULATED_DELAY = 250L

        lateinit var instance: SearchActivity
    }

    override fun getLayoutResId() = R.layout.activity_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupViews()
        setupSearchBar()
    }

    private fun setupViews() {
        setBlurBackground(searchBackground, blurringView)

        resultsOfflineView.layoutManager = LinearLayoutManager(this)
        resultsOnlineView.layoutManager = LinearLayoutManager(this)

        songsAdapter = SongsAdapter(ArrayList(), false) { song, _, _ ->
            playSong(song)
        }

        onlineSongsAdapter = OnlineSongsAdapter(ArrayList()) { song, _, state ->
            when (state) {
                OnlineSongsAdapter.ACTION_DOWNLOAD -> {
                    Toast.makeText(this, "Download clicked!", Toast.LENGTH_LONG).show()
                }

                OnlineSongsAdapter.ACTION_PLAY -> {
                    playSong(song)
                }
            }
        }

        resultsOfflineView.adapter = songsAdapter
        resultsOnlineView.adapter = onlineSongsAdapter
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryChangeListener({ oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                searchBar.clearSuggestions()
            } else {
                searchBar.showProgress()
                SearchDataHelper.findSuggestions(this,
                        newQuery,
                        5,
                        FIND_SUGGESTION_SIMULATED_DELAY,
                        object : SearchDataHelper.OnFindSuggestionsListener {

                            override fun onResults(results: List<SongSearchSuggestion>) {
                                searchBar.swapSuggestions(results)
                                searchBar.hideProgress()
                            }
                        })
            }

            Log.d(TAG, "onSearchTextChanged()")
        })

        searchBar.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                Log.d(TAG, "onSuggestionClicked()")

                SearchDataHelper.addHistory(searchSuggestion.body)
                searchBar.clearSearchFocus()

                searchOffline(searchSuggestion.body)
                searchOnline(searchSuggestion.body)
            }

            override fun onSearchAction(query: String) {
                Log.d(TAG, "onSearchAction()")

                SearchDataHelper.addHistory(query)

                searchOffline(query)
                searchOnline(query)
            }
        })

        searchBar.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {
                Log.d(TAG, "onFocus()")

                searchBar.swapSuggestions(SearchDataHelper.getHistorySuggestions())
            }

            override fun onFocusCleared() {
                Log.d(TAG, "onFocusCleared()")
            }
        })

        searchBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.speech_item_menu) {

            }
        }

        searchBar.setOnHomeActionClickListener({
            Log.d(TAG, "onHomeClicked()")
            val intent = Intent(instance, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })

        searchBar.setOnBindSuggestionCallback { suggestionView, leftIcon, textView, item, itemPosition ->
            val songSuggestion = item as SongSearchSuggestion

            if (songSuggestion.isHistory) {
                leftIcon.setImageDrawable(this.getDrawable(android.R.drawable.ic_menu_recent_history))
            }
        }
    }

    private fun searchOffline(title: String) {
        searchOfflineLoading.visibility = View.VISIBLE
        resultsOfflineLayout.visibility = View.GONE

        doAsync {
            val songs = SongLoader.searchSongs(applicationContext, title, 10)
            if (songs != null) {
                Log.d(TAG, "Results for searching offline: " + songs.size)

                runOnUiThread {
                    songsAdapter?.listSongs = ArrayList<Song>(songs)
                    songsAdapter?.notifyDataSetChanged()
                    searchOfflineLoading.visibility = View.GONE
                    resultsOfflineLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun searchOnline(title: String) {
        val musicListener = object : RequestListener<java.util.ArrayList<Song>> {
            override fun onStart() {
                super.onStart()
                searchOnlineLoading.visibility = View.VISIBLE
                resultsOnlineLayout.visibility = View.GONE
            }

            override fun onComplete(data: java.util.ArrayList<Song>?) {
                if (data != null) {
                    onlineSongsAdapter?.listSongs = data
                    onlineSongsAdapter?.notifyDataSetChanged()
                    searchOnlineLoading.visibility = View.GONE
                    resultsOnlineLayout.visibility = View.VISIBLE
                } else {

                    onlineSongsAdapter?.listSongs = ArrayList()
                    onlineSongsAdapter?.notifyDataSetChanged()
                    searchOnlineLoading.visibility = View.GONE
                    resultsOnlineLayout.visibility = View.VISIBLE
                }
            }
        }

        SongRequest(title, musicListener).execute()
    }

    private fun playSong(song: Song) {
        val songs = ArrayList<Song>()
        songs.add(song)

        CurrentPlaylistManager.listSongs = songs
        CurrentPlaylistManager.position = 0

        MusicPlayer.bind(null)

        val intent = Intent(instance, NowPlayingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
