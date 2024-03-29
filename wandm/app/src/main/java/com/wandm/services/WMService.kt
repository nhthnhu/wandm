package com.wandm.services

import android.app.*
import android.content.*
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.CountDownTimer
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.wandm.App
import com.wandm.IWMService
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.data.CurrentPlaylistManager
import com.wandm.data.PlaybackStatus
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.utils.Constants
import com.wandm.utils.PreferencesUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class WMService : Service(), AudioManager.OnAudioFocusChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private val TAG = "WMService"
        private val NOTIFICATION_ID = 101
        private val NOTIFICATION_CHANNEL = "notification_channel"

        val ACTION_PLAY = "ACTION_PLAY"
        val ACTION_PAUSE = "ACTION_PAUSE"
        val ACTION_NEXT = "ACTION_NEXT"
        val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        val ACTION_STOP = "ACTION_STOP"
    }

    private var notificationManager: NotificationManager? = null

    private val mBinder = ServiceStub()
    private val mPlayer = MultiPlayer()

    private var mediaSessionManager: MediaSessionManager? = null

    private var mediaSession8: MediaSession? = null
    private var transportControls8: android.media.session.MediaController.TransportControls? = null

    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    private var mOngoingCall = false
    private var mPhoneStateListener: PhoneStateListener? = null
    private var mTelephonyManager = App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private var mAudioManager = App.instance.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var playPauseAction: PendingIntent? = null
    private var countdownTimer: CountDownTimer? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PLAY_ACTION -> {
                getNotification(PlaybackStatus.PLAYING)
            }

            MusicEvent.PAUSE_ACTION -> {
                getNotification(PlaybackStatus.PAUSE)
            }

            MusicEvent.RESUME_ACTION -> {
                getNotification(PlaybackStatus.PLAYING)
            }
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p1.equals(PreferencesUtils.ALARM_SET)) {
            val timeStr = PreferencesUtils.getAlarm()
            if (!timeStr.equals("0;;0")) {
                val timeArr = timeStr.split(";;")
                val minute = timeArr[0].toLong()
                val second = timeArr[1].toLong()

                Log.d(TAG, timeStr)

                countdownTimer = object : CountDownTimer((1000 * (minute * 60 + second)), 1000) {
                    override fun onFinish() {
                        mPlayer.pause()
                        PreferencesUtils.setAlarm("0;;0")
                        EventBus.getDefault().post(MessageEvent(MusicEvent.ALARM_OFF))
                        Log.d(TAG, "Finish CountDownTimer")
                    }

                    override fun onTick(p0: Long) {
                    }
                }
                countdownTimer?.start()
            } else {
                Log.d(TAG, "Cancel CountDownTimer")
                countdownTimer?.cancel()
                PreferencesUtils.setAlarm("0;;0")

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        register()
        callStateListener()
        EventBus.getDefault().register(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinder.stop()
        removeAudioFocus()
        stopCallStateListener()
        unregister()
        removeNotification()
        EventBus.getDefault().unregister(this)
    }

    override fun onBind(p0: Intent?): IBinder {
        if (!requestAudioFocus()) {
            stopSelf()
        }
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIncomingActions(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mBinder.stop()
    }

    override fun onAudioFocusChange(p0: Int) {
        Log.d(TAG, p0.toString())
        when (p0) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d(TAG, "AUDIOFOCUS_GAIN")
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS")

                if (mBinder.isPlaying) {
                    mBinder.pause()
                    getNotification(PlaybackStatus.PAUSE)
                } else
                    removeNotification()

            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                if (mBinder.isPlaying) {
                    mBinder.pause()
                    getNotification(PlaybackStatus.PAUSE)
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d(TAG, "AUDIO_FOCUS_LOSS_TRANSIENT_CAN_DUCK")
                if (mBinder.isPlaying)
                    mBinder.setVolume(1.0f, 1.0f)

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

    private fun removeAudioFocus() =
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this)

    inner class ServiceStub : IWMService.Stub() {
        override fun playNext() {
            mPlayer.next()
            updateMetaData()
            getNotification(PlaybackStatus.PLAYING)
        }

        override fun playPre() {
            mPlayer.pre()
            updateMetaData()
            getNotification(PlaybackStatus.PLAYING)
        }

        override fun stop() {
            mPlayer.stop()
            stopSelf()
        }

        override fun pause() {
            mPlayer.pause()
        }

        override fun play() {
            mPlayer.play()
        }

        override fun resume() {
            mPlayer.resume()
        }

        override fun isPlaying(): Boolean {
            return mPlayer.isPlaying()
        }

        override fun playNew() {
            mPlayer.playNew()
            initMediaSession()
            updateMetaData()
            getNotification(PlaybackStatus.PLAYING)
        }

        override fun duration() = mPlayer.duration()

        override fun position() = mPlayer.position()

        override fun seekTo(position: Int) {
            mPlayer.seekTo(position)
        }

        override fun setVolume(left: Float, right: Float) {
            mPlayer.setVolume(left, right)
        }
    }

    private fun initMediaSession() {
        if (mediaSessionManager != null || mediaSession8 != null)
            return
        else {
            mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            mediaSession = MediaSessionCompat(applicationContext, "WAndM")
            mediaSession8 = MediaSession(applicationContext, "WAndM")

            transportControls = mediaSession!!.controller.transportControls
            transportControls8 = mediaSession8!!.controller.transportControls


            mediaSession!!.isActive = true
            mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            mediaSession8!!.isActive = true

            updateMetaData()


            mediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    mBinder.resume()
                    getNotification(PlaybackStatus.PLAYING)
                }

                override fun onPause() {
                    super.onPause()
                    mBinder.pause()
                    getNotification(PlaybackStatus.PAUSE)
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()
                    mBinder.playNext()
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    mBinder.playPre()
                }

                override fun onStop() {
                    super.onStop()
                    removeNotification()
                }
            })
        }
    }


    private fun updateMetaData() {
        val song = CurrentPlaylistManager.currentSong
        mediaSession!!.setMetadata(MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song?.title)
                .build())
    }

    private fun getNotification(playbackStatus: PlaybackStatus) {
        val notificationAction: Int
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.ic_action_pause_dark
            playPauseAction = playbackAction(1)
        } else {
            notificationAction = R.drawable.ic_action_play_dark
            playPauseAction = playbackAction(0)
        }

        val song = CurrentPlaylistManager.currentSong

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {

            val notificationBuilder = NotificationCompat.Builder(this)
                    .setShowWhen(false)
                    .setColor(resources.getColor(R.color.color_primary_dark))
                    .setSmallIcon(R.drawable.ic_music)
                    .setContentText(song?.title)
                    .addAction(R.drawable.ic_action_skip_pre_dark, "previous", playbackAction(3))
                    .addAction(notificationAction, "pause", playPauseAction)
                    .addAction(R.drawable.ic_action_skip_next_dark, "next", playbackAction(2))
                    .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession!!.sessionToken)
                            .setShowActionsInCompactView(0, 1, 2))
                    .setDeleteIntent(playbackAction(4))
                    .setContentIntent(PendingIntent.getActivity(this, 0,
                            Intent(this, NowPlayingActivity::class.java)
                            , PendingIntent.FLAG_UPDATE_CURRENT))

            notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
        } else {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL,
                    resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val builder = Notification.Builder(this, NOTIFICATION_CHANNEL)
                    .setColor(resources.getColor(R.color.color_primary_dark))
                    .setSmallIcon(R.drawable.ic_music)
                    .setContentText(song?.title)
                    .addAction(R.drawable.ic_action_skip_pre_dark, "previous", playbackAction(3))
                    .addAction(notificationAction, "pause", playPauseAction)
                    .addAction(R.drawable.ic_action_skip_next_dark, "next", playbackAction(2))
                    .setStyle(Notification.MediaStyle()
                            .setMediaSession(mediaSession8!!.sessionToken)
                            .setShowActionsInCompactView(0, 1, 2))
                    .setDeleteIntent(playbackAction(4))
                    .setContentIntent(PendingIntent.getActivity(this, 0,
                            Intent(this, NowPlayingActivity::class.java)
                            , PendingIntent.FLAG_UPDATE_CURRENT))

            notificationManager?.createNotificationChannel(channel)

            notificationManager?.notify(NOTIFICATION_ID, builder.build())


        }

    }

    private fun removeNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        mPlayer.stop()
        stopSelf()
        EventBus.getDefault().post(MessageEvent(MusicEvent.REMOVE_NOTI_ACTION))
    }

    private fun handleIncomingActions(playbackAction: Intent?) {
        if (playbackAction == null || playbackAction.action == null)
            return

        val actionString = playbackAction.action
        when (actionString) {
            ACTION_PLAY -> transportControls?.play()
            ACTION_PAUSE -> transportControls?.pause()
            ACTION_NEXT -> transportControls?.skipToNext()
            ACTION_PREVIOUS -> transportControls?.skipToPrevious()
            ACTION_STOP -> transportControls?.stop()
        }
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        val playbackAction = Intent(this, WMService::class.java)
        when (actionNumber) {
            0 -> {
                playbackAction.action = ACTION_PLAY
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            1 -> {
                playbackAction.action = ACTION_PAUSE
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            2 -> {

                playbackAction.action = ACTION_NEXT
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            3 -> {

                playbackAction.action = ACTION_PREVIOUS
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            4 -> {
                playbackAction.action = ACTION_STOP
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
        }
        return null
    }

    private fun callStateListener() {
        mPhoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> {
                        mBinder.pause()
                        mOngoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (mOngoingCall) {
                            mOngoingCall = false
                            mBinder.resume()
                        }
                    }
                }
            }
        }
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun stopCallStateListener() {
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE)
    }


    private fun register() {
        val noisyIntent = IntentFilter(Constants.ACTION_BECOMING_NOISY)
        LocalBroadcastManager.getInstance(App.instance).
                registerReceiver(mBecomingNoisyReceiver, noisyIntent)

        val removeNotiIntent = IntentFilter(Constants.REMOVE_MUSIC_NOTIFY)
        LocalBroadcastManager.getInstance(App.instance).
                registerReceiver(mRemoveNotiReceiver, removeNotiIntent)

        PreferencesUtils.mPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    private fun unregister() {
        LocalBroadcastManager.getInstance(App.instance).unregisterReceiver(mBecomingNoisyReceiver)
        LocalBroadcastManager.getInstance(App.instance).unregisterReceiver(mRemoveNotiReceiver)
        PreferencesUtils.mPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private val mBecomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mBinder.pause()
            getNotification(PlaybackStatus.PAUSE)
        }
    }

    private val mRemoveNotiReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            removeNotification()
        }
    }
}