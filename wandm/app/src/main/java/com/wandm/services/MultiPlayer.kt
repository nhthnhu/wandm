package com.wandm.services

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.wandm.App
import com.wandm.data.CurrentPlaylistManager

class MultiPlayer : MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    private var mPlayer = MediaPlayer()
    private var mAudioManager = App.instance.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mPosition = 0

    private var mOngoingCall = false
    private var mPhoneStateListener: PhoneStateListener? = null
    private var mTelephonyManager = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


    private val TAG = "MultiPlayer"

    override fun onCompletion(p0: MediaPlayer?) {
        next()
    }

    override fun onPrepared(p0: MediaPlayer?) {
        play()
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        when (p1) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + p2)
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + p2)
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + p2)
        }
        return false
    }

    override fun onAudioFocusChange(p0: Int) {
        when (p0) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d(TAG, "AUDIOFOCUS_GAIN")
                if (mPlayer.isPlaying == false) {
                    resume()
                }

                mPlayer.setVolume(1.0f, 1.0f)
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS")

                if (mPlayer.isPlaying) {
                    pause()
                }

            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                if (mPlayer.isPlaying) {
                    pause()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                if (mPlayer.isPlaying)
                    mPlayer.setVolume(0.1f, 0.1f)
            }

            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> {
                Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE")
            }
        }
    }

    fun requestAudioFocus(): Boolean {
        val result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }
        return false
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this)
    }

    fun callStateListener() {
        mPhoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> {
                        pause()
                        mOngoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (mOngoingCall) {
                            mOngoingCall = false
                            resume()
                        }
                    }
                }
            }
        }
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE)
    }

    fun stopCallStateListener() {
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE)
    }


    private fun init(dataSource: String) {
        mPlayer = MediaPlayer()

        mPlayer.setOnCompletionListener(this)
        mPlayer.setOnErrorListener(this)
        mPlayer.setOnPreparedListener(this)

        mPlayer.reset()

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
            mPlayer.setDataSource(dataSource)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer.prepareAsync()
    }

    fun play() {
        if (!mPlayer.isPlaying)
            mPlayer.start()
    }

    fun pause() {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            mPosition = mPlayer.currentPosition
        }
    }

    fun resume() {
        if (!mPlayer.isPlaying) {
            mPlayer.seekTo(mPosition)
            mPlayer.start()
        }
    }

    fun stop() {
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }

        mPlayer.release()
    }

    fun next() {
        val song = CurrentPlaylistManager.next()
        stop()
        init(song.data)

    }

    fun pre() {
        val song = CurrentPlaylistManager.previous()
        stop()
        init(song.data)
    }

    fun playNew() {
        val song = CurrentPlaylistManager.mSong
        stop()
        mPlayer.reset()
        init(song.data)
    }

    fun duration(): Int {
        return mPlayer.duration
    }

    fun seekTo(postion: Int) {
        mPlayer.seekTo(postion)
    }

    fun position(): Int {
        return mPlayer.currentPosition
    }

    fun isPlaying() = mPlayer.isPlaying

}