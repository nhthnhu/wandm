package com.wandm.services

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.wandm.data.CurrentPlaylistManager
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.utils.PreferencesUtils
import org.greenrobot.eventbus.EventBus

class MultiPlayer : MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private val TAG = "MultiPlayer"

    private var mPlayer = MediaPlayer()
    private var mPosition = 0


    override fun onCompletion(p0: MediaPlayer?) {
        EventBus.getDefault().post(MessageEvent(MusicEvent.COMPLETED_ACTION))
        if (PreferencesUtils.getRepeatMode() == 1 ||
                CurrentPlaylistManager.position < CurrentPlaylistManager.listSongs.size) {
            next()
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        play()
        EventBus.getDefault().post(MessageEvent(MusicEvent.PREPARED_ACTION))
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        when (p1) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + p2)
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + p2)
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + p2)
        }
        return false
    }


    fun init(dataSource: String?) {
        mPlayer = MediaPlayer()

        mPlayer.setOnCompletionListener(this)
        mPlayer.setOnErrorListener(this)
        mPlayer.setOnPreparedListener(this)

        mPlayer.reset()

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
            mPlayer.setDataSource(dataSource)
        } catch (e: Exception) {
        }

        mPlayer.prepareAsync()
    }

    fun play() {
        try {
            if (!mPlayer.isPlaying) {
                mPlayer.start()
                EventBus.getDefault().post(MessageEvent(MusicEvent.PLAY_ACTION))
            }
        } catch (e: Exception) {

        }
    }

    fun pause() {
        try {
            if (mPlayer.isPlaying) {
                mPlayer.pause()
                mPosition = mPlayer.currentPosition
                EventBus.getDefault().post(MessageEvent(MusicEvent.PAUSE_ACTION))
            }
        } catch (e: Exception) {
        }
    }

    fun resume() {
        try {
            if (!mPlayer.isPlaying) {
                mPlayer.seekTo(mPosition)
                mPlayer.start()
                EventBus.getDefault().post(MessageEvent(MusicEvent.RESUME_ACTION))
            }
        } catch (e: Exception) {
        }
    }

    fun stop() {
        try {
            if (mPlayer.isPlaying) {
                mPlayer.stop()
            }
            mPlayer.release()
        } catch (e: Exception) {
        }
    }

    fun next() {
        val song = CurrentPlaylistManager.next()
        stop()
        init(song?.data)
    }

    fun pre() {
        val song = CurrentPlaylistManager.previous()
        stop()
        init(song?.data)
    }

    fun playNew() {
        val song = CurrentPlaylistManager.currentSong
        stop()
        init(song?.data)
    }

    fun duration(): Int {
        try {
            return mPlayer.duration
        } catch (e: Exception) {
            return 0
        }
    }

    fun seekTo(postion: Int) {
        try {
            mPlayer.seekTo(postion)
        } catch (e: Exception) {
        }
    }

    fun position(): Int {
        try {
            return mPlayer.currentPosition
        } catch (e: Exception) {
            return 0
        }
    }

    fun isPlaying(): Boolean {
        try {
            return mPlayer.isPlaying
        } catch (e: Exception) {
            return false
        }
    }

    fun release() {
        try {
            mPlayer.reset()
            mPlayer.release()
        } catch (e: Exception) {
        }
    }

    fun setVolume(left: Float, right: Float) {
        try {
            mPlayer.setVolume(left, right)
        } catch (e: Exception) {
        }
    }

}