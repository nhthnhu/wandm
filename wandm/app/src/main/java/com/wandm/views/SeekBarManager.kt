package com.wandm.views

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import com.wandm.R
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.fragments.BaseFragment
import com.wandm.services.MusicPlayer
import kotlinx.android.synthetic.main.music_seekbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class SeekBarManager : BaseFragment() {

    private val TAG = "SeekBarManager"

    var mServiceBound = false
    var mDuration = 0
    var mStartTime = 0
        set(value) {
            field = value
            mFinalTime = value + 1
        }
    var mFinalTime = 0
    private var mHandler: Handler? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                mServiceBound = true
                mHandler = Handler()

                mDuration = MusicPlayer.duration()
                mFinalTime = mDuration + 1

                songProgress.isEnabled = true

                songProgress.max = mFinalTime

                seekBarLayout.visibility = View.VISIBLE

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
        }
    }


    override fun getLayoutResId(): Int {
        return R.layout.music_seekbar
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
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

                songProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        MusicPlayer.seekTo(seekBar.progress)
                    }
                })

            }
        }
    }

    fun stop() {
        mServiceBound = false
        mHandler?.removeCallbacks(mUpdateSongTime)
    }

    fun play() {

        if (mHandler != null)
            mHandler!!.postDelayed(mUpdateSongTime, 100)
    }
}
