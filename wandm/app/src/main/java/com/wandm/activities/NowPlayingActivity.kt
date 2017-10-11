package com.wandm.activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.services.MusicPlayer
import kotlinx.android.synthetic.main.activity_now_playing.*
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class NowPlayingActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "NowPlayingActivity"

    private var mServiceBound = false
    private var mDuration = 0
    private var mStartTime = 0
        set(value) {
            field = value
            mFinalTime = value + 1
        }
    private var mFinalTime = 0
    private var mHandler: Handler? = null

    private var isShuffle = false
    private var isRepeat = 0
    private var isFavorite = false


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                Log.d(TAG, "Prepared_action")
                titleSongTextView.text = CurrentPlaylistManager.mSong.title
                artistSongTextView.text = CurrentPlaylistManager.mSong.artistName
                preparedSeekBar()
                albumImage.start()
            }

            MusicEvent.PLAY_ACTION -> {
                playpauseButton.isPlayed = true
                playpauseButton.startAnimation()
                albumImage.start()
            }

            MusicEvent.RESUME_ACTION -> {
                playpauseButton.isPlayed = true
                playpauseButton.startAnimation()
                albumImage.start()
            }

            MusicEvent.PAUSE_ACTION -> {
                playpauseButton.isPlayed = false
                playpauseButton.startAnimation()
                albumImage.stop()
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_now_playing
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupToolbar()
        EventBus.getDefault().register(this)

        playpauseButton.setOnClickListener(this)
        playpauseWrapper.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        preButton.setOnClickListener(this)
        shuffleButton.setOnClickListener(this)
        repeatButton.setOnClickListener(this)
        favoriteButton.setOnClickListener(this)
        albumImage.setOnClickListener(this)

        albumImage.setCoverDrawable(R.drawable.ic_action_head_set_light)
        albumImage.setAutoProgress(false)
        albumImage.setProgressVisibility(false)

        artistSongTextView.text = CurrentPlaylistManager.mSong.artistName
        titleSongTextView.text = CurrentPlaylistManager.mSong.title
        artistSongTextView.isSelected = true
        titleSongTextView.isSelected = true

        mHandler = Handler()


        if (MusicPlayer.isPlaying()) {
            playpauseButton.isPlayed = true
            preparedSeekBar()
            albumImage.start()
        } else {
            playpauseButton.isPlayed = false
            albumImage.stop()
        }

        playpauseButton.startAnimation()

    }

    override fun onResume() {
        super.onResume()
        setBlurBackground(nowPlayingBackground, nowPlayingBlurringView)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        stop()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbarNowPlaying)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
        }
    }

    private val mUpdateSongTime = object : Runnable {
        override fun run() {
            if (mServiceBound == true) {
                mStartTime = MusicPlayer.position()

                songProgress.progress = mStartTime

                val minute = TimeUnit.MILLISECONDS.toMinutes(mStartTime.toLong()).toInt()
                val second = (TimeUnit.MILLISECONDS.toSeconds(mStartTime.toLong()) - TimeUnit.MINUTES.
                        toSeconds(TimeUnit.MILLISECONDS.toMinutes(mStartTime.toLong()))).toInt()

                if (songElapsedTime != null && songProgress != null && mHandler != null) {

                    if (minute < 10 && second < 10)
                        songElapsedTime.text = String.format("0$minute:0$second")
                    else if (minute < 10 && second >= 10)
                        songElapsedTime.text = "0$minute:$second"
                    else if (minute >= 10 && second >= 10)
                        songElapsedTime.text = minute.toString() + ":" + second

                    mHandler?.postDelayed(this, 100)
                }

            }
        }
    }

    private fun stop() {
        mServiceBound = false
        mHandler?.removeCallbacks(mUpdateSongTime)
    }

    private fun play() {
        if (mHandler != null)
            mHandler!!.postDelayed(mUpdateSongTime, 100)

        songProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MusicPlayer.seekTo(seekBar.progress)
            }
        })
    }

    private fun preparedSeekBar() {
        mServiceBound = true

        mDuration = MusicPlayer.duration()
        mFinalTime = mDuration + 1

        songProgress.isEnabled = true

        songProgress.max = mFinalTime
        val minute = TimeUnit.MILLISECONDS.toMinutes(mFinalTime.toLong()).toInt()
        val second = (TimeUnit.MILLISECONDS.toSeconds(mFinalTime.toLong()) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mFinalTime.toLong()))).toInt()

        if (minute < 10 && second < 10)
            songDuration.text = "0$minute:0$second"
        else if (minute < 10 && second >= 10)
            songDuration.text = "0$minute:$second"
        else if (minute >= 10 && second >= 10)
            songDuration.text = minute.toString() + ":" + second

        play()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.playpauseButton -> {
                MusicPlayer.playOrPause()
                playpauseButton.startAnimation()
            }

            R.id.playpauseWrapper -> {
                MusicPlayer.playOrPause()
                playpauseButton.startAnimation()
            }

            R.id.nextButton -> {
                MusicPlayer.next()
            }

            R.id.preButton -> {
                MusicPlayer.pre()
            }

            R.id.shuffleButton -> {
                if (isShuffle) {
                    isShuffle = false
                    shuffleButton.setColorResource(R.color.color_white)
                } else {
                    isShuffle = true
                    shuffleButton.setColorResource(R.color.color_primary_dark)
                }
            }

            R.id.repeatButton -> {
                if (isRepeat == 0) {
                    isRepeat++
                    repeatButton.setColorResource(R.color.color_primary_dark)
                } else if (isRepeat == 1) {
                    isRepeat++
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
                    repeatButton.setColorResource(R.color.color_primary_dark)
                } else {
                    isRepeat = 0
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                    repeatButton.setColorResource(R.color.color_white)
                }
            }

            R.id.favoriteButton -> {
                if (isFavorite) {
                    isFavorite = false
                    favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART_OUTLINE)
                    favoriteButton.setColorResource(R.color.color_white)
                } else {
                    isFavorite = true
                    favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART)
                    favoriteButton.setColorResource(R.color.color_red)
                }
            }

            R.id.albumImage -> {
                if (albumImage.isRotating) {
                    albumImage.stop()
                } else
                    albumImage.start()
                MusicPlayer.playOrPause()
            }
        }
    }

}