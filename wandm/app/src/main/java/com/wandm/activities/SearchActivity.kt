package com.wandm.activities

import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
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
import com.wandm.data.SearchHelper
import com.wandm.loaders.SongLoader
import com.wandm.models.RequestListener
import com.wandm.models.SongSearchSuggestion
import com.wandm.models.song.Song
import com.wandm.models.song.SongRequest
import com.wandm.permissions.PermissionCallback
import com.wandm.permissions.askPermission
import com.wandm.services.MusicPlayer
import com.wandm.speech.GoogleVoiceTypingDisabledException
import com.wandm.speech.Speech
import com.wandm.speech.SpeechDelegate
import com.wandm.speech.SpeechRecognitionNotAvailable
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : BaseActivity() {

    private var songsAdapter: SongsAdapter? = null
    private var onlineSongsAdapter: OnlineSongsAdapter? = null

    companion object {
        private val TAG = "SearchActivity"
        private val FIND_SUGGESTION_SIMULATED_DELAY = 250L
        private val SPEECH_LISTENING = "speech_listening"
        private val SPEECH_DONE = "speech_done"
        private val SPEECH_ERROR = "speech_error"
        private val SPEECH_START = "speech_start"
        private val SPEECH_UPDATE_WAVE = "speech_update_wave"

        lateinit var instance: SearchActivity
    }

    override fun getLayoutResId() = R.layout.activity_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupViews()
        setupSize()
        setupSearchBar()
        setupSpeech()
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

        waveformView.setOnClickListener {
            if (Speech.getInstance().isListening) {
                Speech.getInstance().stopListening()
            }
        }
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryChangeListener({ oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") {
                searchBar.clearSuggestions()
            } else {
                searchBar.showProgress()
                SearchHelper.findSuggestions(this,
                        newQuery,
                        5,
                        FIND_SUGGESTION_SIMULATED_DELAY,
                        object : SearchHelper.OnFindSuggestionsListener {

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

                SearchHelper.addHistory(searchSuggestion.body)
                searchBar.clearSearchFocus()

                searchOffline(searchSuggestion.body)
                searchOnline(searchSuggestion.body)
            }

            override fun onSearchAction(query: String) {
                Log.d(TAG, "onSearchAction()")

                SearchHelper.addHistory(query)

                searchOffline(query)
                searchOnline(query)
            }
        })

        searchBar.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {
                Log.d(TAG, "onFocus()")

                searchBar.swapSuggestions(SearchHelper.getHistorySuggestions())
            }

            override fun onFocusCleared() {
                Log.d(TAG, "onFocusCleared()")
            }
        })

        searchBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.speech_item_menu) {
                startSpeech()
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

    private fun setupSpeech() {

        Speech.init(this)
        Speech.getInstance().setLocale(Locale.getDefault())
        Speech.getInstance().setTransitionMinimumDelay(250)
        Speech.getInstance().setDelegate(object : SpeechDelegate {
            override fun onStartOfSpeech() {
                showViewsForSpeech(SPEECH_START)
            }

            override fun onSpeechRmsChanged(value: Float) {
                showViewsForSpeech(SPEECH_UPDATE_WAVE, (value / 20).toString())
            }

            override fun onSpeechPartialResults(results: List<String>) {
                showViewsForSpeech(SPEECH_LISTENING, results[0])
            }

            override fun onSpeechResult(result: String) {
                Log.d(TAG, "SpeechResult: " + result)

                if (result != "") {
                    searchOffline(result)
                    searchOnline(result)
                    showViewsForSpeech(SPEECH_DONE)
                    searchBar.setSearchText(result)
                    return
                }
            }

            override fun onError(code: Int) {
                val errorMessage = getErrorText(code)
                showViewsForSpeech(SPEECH_ERROR, errorMessage)
            }
        })

    }

    private fun startSpeech() {
        val permission = android.Manifest.permission.RECORD_AUDIO

        askPermission(permission, object : PermissionCallback {
            override fun permissionGranted() {

            }

            override fun permissionRefused() {
                finish()
            }
        })

        if (Speech.getInstance().isListening) {
            Speech.getInstance().stopListening()
            return
        }

        try {
            Speech.getInstance().startListening()
        } catch (speechRecognitionNotAvailable: SpeechRecognitionNotAvailable) {
            Log.e(TAG, "Speech Recognition isn't Available")
        } catch (e: GoogleVoiceTypingDisabledException) {
            Log.e(TAG, "Google Voice Typing was disabled")
        }

    }

    private fun getErrorText(errorCode: Int): String {

        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> return getString(R.string.error_audio)

            SpeechRecognizer.ERROR_CLIENT -> return getString(R.string.error_client)

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> return getString(R.string.error_client)

            SpeechRecognizer.ERROR_NETWORK -> return getString(R.string.error_network)

            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> return getString(R.string.error_network_timeout)

            SpeechRecognizer.ERROR_NO_MATCH -> return getString(R.string.error_no_match)

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> return getString(R.string.error_recognizer_busy)

            SpeechRecognizer.ERROR_SERVER -> return getString(R.string.error_server)

            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> return getString(R.string.error_speech_timeout)

            else -> return getString(R.string.error_default)
        }
    }

    private fun showViewsForSpeech(state: String, value: String = "") {
        when (state) {

            SPEECH_START -> {
                speechView.visibility = View.VISIBLE

                textListening.text = ""
                textListening.visibility = View.INVISIBLE

                textMessage.text = ""
                textMessage.visibility = View.INVISIBLE

                waveformView.reset()
                waveformView.visibility = View.VISIBLE
            }

            SPEECH_LISTENING -> {
                textListening.text = value
                textListening.visibility = View.VISIBLE

                textMessage.visibility = View.INVISIBLE
            }

            SPEECH_UPDATE_WAVE -> {
                waveformView.updateAmplitude(value.toFloat())
            }

            SPEECH_ERROR -> {
                textMessage.text = value
                textMessage.visibility = View.VISIBLE

                textListening.visibility = View.INVISIBLE
                waveformView.visibility = View.GONE
            }

            SPEECH_DONE -> {
                speechView.visibility = View.GONE
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

    private fun setupSize() {
        val textSize = PreferencesUtils.getTextSize()
        textMessage.textSize = textSize.toFloat()
        textListening.textSize = textSize.toFloat()
        labelSearchOffline.textSize = textSize.toFloat()
        labelSearchOnline.textSize = textSize.toFloat()
    }
}
