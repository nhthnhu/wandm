package com.wandm.services

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.wandm.data.CurrentPlaylistManager

class MultiPlayer : MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private var mPlayer = MediaPlayer()
    private var mPosition = 0


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


    fun init(dataSource: String) {
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

    fun release() {
        mPlayer.reset()
        mPlayer.release()
    }

    fun setVolume(left: Float, right: Float) {
        mPlayer.setVolume(left, right)
    }

}