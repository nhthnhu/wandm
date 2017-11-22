package com.wandm.fragments

import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.wandm.App
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.adapters.SearchAdapter
import com.wandm.data.CurrentPlaylistManager
import com.wandm.dialogs.BaseDialogFragment
import com.wandm.models.RequestListener
import com.wandm.models.song.Song
import com.wandm.models.song.SongRequest
import com.wandm.permissions.PermissionCallback
import com.wandm.permissions.askPermission
import com.wandm.services.MusicPlayer
import com.wandm.speech.GoogleVoiceTypingDisabledException
import com.wandm.speech.Speech
import com.wandm.speech.SpeechDelegate
import com.wandm.speech.SpeechRecognitionNotAvailable
import kotlinx.android.synthetic.main.dialog_search.*
import java.util.*


class SearchDialogFragment : BaseDialogFragment(), View.OnClickListener {

    private val TAG = "SearchDialogFragment"
    private var listSong: ArrayList<Song>? = null

    companion object {
        private val DEFAULT_TYPE = 0
        private val LISTENING_TYPE = 1
        private val LOADING_TYPE = 2
        private val ERROR_TYPE = 3

        private val listNames = ArrayList<String>()
        private var adapter: ArrayAdapter<String>? = null

        fun newInstance(): SearchDialogFragment {
            val fragment = SearchDialogFragment()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setupSpeech()
        return inflater?.inflate(R.layout.dialog_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        waveformView.setOnClickListener(this)

        listNames.add("Cham khe tim anh mot chut thoi")
        listNames.add("Cham moi em roi")
        listNames.add("Hom nay")
        listNames.add("Hom qua")
        listNames.add("Hom kia")


        adapter = ArrayAdapter(context, R.layout.item_auto_complete_song, listNames)
        keywordAutoCompleteTextView.setAdapter(adapter)
        keywordAutoCompleteTextView.setDropDownBackgroundDrawable(context.getDrawable(R.drawable.background_search))

        keywordAutoCompleteTextView.setOnEditorActionListener({ v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchOnline()
                handled = true
            }
            handled
        })

        speechToTextButton.setOnClickListener {
            startSpeech()
            keywordAutoCompleteTextView.setText("")
        }

    }

    private fun setupSpeech() {
        Speech.getInstance().setLocale(Locale("vi"))
        Speech.getInstance().setTransitionMinimumDelay(250)
        Speech.getInstance().setDelegate(object : SpeechDelegate {
            override fun onStartOfSpeech() {
                showView(SearchDialogFragment.LISTENING_TYPE, "")
            }

            override fun onSpeechRmsChanged(value: Float) {
                waveformView.updateAmplitude(value / 20)
            }

            override fun onSpeechPartialResults(results: List<String>) {
                showView(SearchDialogFragment.LISTENING_TYPE, results[0])
            }

            override fun onSpeechResult(result: String) {
                Log.d(TAG, "SpeechResult: " + result)

                if (result == "") {
                    waveformView.visibility = View.GONE
                    waveformView.reset()
                    return
                }

                keywordAutoCompleteTextView.setText(result)
                searchOnline()
                showView(LOADING_TYPE, "")
            }

            override fun onError(code: Int) {
                val errorMessage = getErrorText(code)
                showView(SearchDialogFragment.ERROR_TYPE, errorMessage)
            }
        })

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.waveformView -> {
                if (Speech.getInstance().isListening) {
                    Speech.getInstance().stopListening()
                    showView(DEFAULT_TYPE, "")
                } else {
                    startSpeech()
                    showView(LISTENING_TYPE, "")
                }
            }
        }
    }


    private fun showView(type: Int, message: String) {
        when (type) {
            DEFAULT_TYPE -> {
                textError.visibility = View.GONE
                keywordAutoCompleteTextView.setText("")
                textListening.visibility = View.GONE
                waveformView.visibility = View.GONE
                waveformView.reset()
            }

            LISTENING_TYPE -> {
                textError.visibility = View.GONE
                textListening.text = message
                textListening.visibility = View.VISIBLE
                waveformView.visibility = View.VISIBLE
            }

            LOADING_TYPE -> {
                textError.visibility = View.GONE
                textListening.text = ""
                textListening.visibility = View.GONE
                waveformView.visibility = View.GONE
            }

            ERROR_TYPE -> {
                textError.text = message
                textError.visibility = View.VISIBLE
                textListening.text = ""
                textListening.visibility = View.GONE
                waveformView.visibility = View.GONE
                waveformView.reset()
            }
        }
    }

    private fun startSpeech() {
        val permission = android.Manifest.permission.RECORD_AUDIO

        askPermission(permission, object : PermissionCallback {
            override fun permissionGranted() {

            }

            override fun permissionRefused() {
                activity.finish()
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

    private fun searchOnline() {
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
        SongRequest(keywordAutoCompleteTextView.text.toString(), musicListener).execute()
    }
}