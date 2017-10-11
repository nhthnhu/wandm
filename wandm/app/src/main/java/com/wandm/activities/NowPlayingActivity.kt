package com.wandm.activities

import android.os.Bundle
import android.util.Log
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import kotlinx.android.synthetic.main.activity_now_playing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NowPlayingActivity : BaseActivity() {

    private val TAG = "NowPlayingActivity"

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                Log.d(TAG, "Prepared_action")
                titleSongTextView.text = CurrentPlaylistManager.mSong.title
                artistSongTextView.text = CurrentPlaylistManager.mSong.artistName
            }

            MusicEvent.PLAY_ACTION -> {
                playpauseButton.isPlayed = true
                playpauseButton.startAnimation()
            }

            MusicEvent.RESUME_ACTION -> {
                playpauseButton.isPlayed = true
                playpauseButton.startAnimation()
            }

            MusicEvent.PAUSE_ACTION -> {
                playpauseButton.isPlayed = false
                playpauseButton.startAnimation()
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_now_playing
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupToolbar()
        EventBus.getDefault().register(this)
        Log.d(TAG, "initView")
    }

    override fun onResume() {
        super.onResume()
        setBlurBackground(nowPlayingBackground, nowPlayingBlurringView)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbarNowPlaying)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
        }
    }

}