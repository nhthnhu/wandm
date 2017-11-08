package com.wandm.fragments

import android.app.DialogFragment
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.permissions.PermissionCallback
import com.wandm.permissions.askPermission
import com.wandm.speech.GoogleVoiceTypingDisabledException
import com.wandm.speech.Speech
import com.wandm.speech.SpeechDelegate
import com.wandm.speech.SpeechRecognitionNotAvailable
import kotlinx.android.synthetic.main.dialog_speech.*
import java.util.*

class SpeechDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {
        private val TAG = "SpeechDialogFragment"
        private val DEFAULT_TYPE = 0
        private val LISTENING_TYPE = 1
        private val LOADING_TYPE = 2
        private val ERROR_TYPE = 3

        var callback: Callback? = null
            set(value) {
                field = value
            }

        fun newInstance(): SpeechDialogFragment {
            val fragment = SpeechDialogFragment()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.dialog_speech, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpeech()
        startSpeech()
        showView(LISTENING_TYPE, "")
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


    private fun setupSpeech() {
        Speech.init(context)
        Speech.getInstance().setLocale(Locale("vi"))
        Speech.getInstance().setTransitionMinimumDelay(250)
        Speech.getInstance().setDelegate(object : SpeechDelegate {
            override fun onStartOfSpeech() {
                showView(LISTENING_TYPE, "")
            }

            override fun onSpeechRmsChanged(value: Float) {
                waveformView.updateAmplitude(value / 20)
            }

            override fun onSpeechPartialResults(results: List<String>) {
                showView(LISTENING_TYPE, results[0])
            }

            override fun onSpeechResult(result: String) {
                Log.d(TAG, "SpeechResult: " + result)

                if (result == "") {
                    waveformView.visibility = View.INVISIBLE
                    waveformView.reset()
                    return
                }

                callback?.onResult(result)
                showView(LOADING_TYPE, "")
            }

            override fun onError(code: Int) {
                val errorMessage = getErrorText(code)
                showView(ERROR_TYPE, errorMessage)
            }
        })

    }

    private fun showView(type: Int, message: String) {
        when (type) {
            DEFAULT_TYPE -> {
                textListening.text = ""
                textListening.visibility = View.INVISIBLE
                waveformView.visibility = View.INVISIBLE
                waveformView.reset()
            }

            LISTENING_TYPE -> {
                textListening.text = message
                textListening.visibility = View.VISIBLE
                waveformView.visibility = View.VISIBLE
            }

            LOADING_TYPE -> {
                textListening.text = ""
                textListening.visibility = View.INVISIBLE
                waveformView.visibility = View.INVISIBLE
            }

            ERROR_TYPE -> {
                textListening.text = ""
                textListening.visibility = View.INVISIBLE
                waveformView.visibility = View.INVISIBLE
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


    interface Callback {
        fun onRelease()

        fun onResult(result: String)
    }

}