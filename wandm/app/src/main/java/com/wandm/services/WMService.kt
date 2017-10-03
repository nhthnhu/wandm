package com.wandm.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.wandm.data.CurrentPlaylistManager

class WMService : Service(), MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_STOP = "ACTION_STOP"

        private var mMediaPlayer: MediaPlayer = MediaPlayer()
        private var mResumePosition: Int = 0

        private val NOTIFICATION_ID = 101

        private var playPauseAction: PendingIntent? = null

        private lateinit var mAudioManager: AudioManager


        private var ongoingCall = false
        private lateinit var phoneStateListener: PhoneStateListener
        private lateinit var telephonyManager: TelephonyManager

        private lateinit var mediaSessionManager: MediaSessionManager
        private lateinit var mediaSession: MediaSessionCompat
        private lateinit var transportControls: MediaControllerCompat.TransportControls
    }

    private val TAG = "MediaPlayerService"

    private val mIBinder: Binder = LocalBinder()

    inner class LocalBinder : Binder() {
        val service: WMService
            get() = this@WMService
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(p0: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPrepared(p0: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSeekComplete(p0: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInfo(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBufferingUpdate(p0: MediaPlayer?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAudioFocusChange(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initMediaPlayer() {

        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnErrorListener(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnBufferingUpdateListener(this)
        mMediaPlayer.setOnSeekCompleteListener(this)
        mMediaPlayer.setOnInfoListener(this)

        mMediaPlayer.reset()

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
            mMediaPlayer.setDataSource(CurrentPlaylistManager.mSong)
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }

        mMediaPlayer?.prepareAsync()
    }


}