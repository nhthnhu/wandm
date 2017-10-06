package com.wandm.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.data.CurrentPlaylistManager
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.services.MusicPlayer
import com.wandm.services.MusicPlayer.playOrPause
import kotlinx.android.synthetic.main.fragment_quick_control.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class QuickControlFragment : BaseFragment(), View.OnClickListener {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                controlFragment.visibility = View.VISIBLE
                titleSongTextView.text = CurrentPlaylistManager.mSong.title
                artistSongTextView.text = CurrentPlaylistManager.mSong.artistName
            }

            MusicEvent.PLAY_ACTION -> {
                playPauseButton.isPlayed = true
                playPauseButton.startAnimation()
            }

            MusicEvent.RESUME_ACTION -> {
                playPauseButton.isPlayed = true
                playPauseButton.startAnimation()
            }

            MusicEvent.PAUSE_ACTION -> {
                playPauseButton.isPlayed = false
                playPauseButton.startAnimation()
            }

            MusicEvent.REMOVE_NOTI_ACTION -> {
                controlFragment.visibility = View.GONE
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_quick_control
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        nextButton.setOnClickListener(this)
        preButton.setOnClickListener(this)
        playPauseButton.setOnClickListener(this)
        playPauseWrapper.setOnClickListener(this)
        controlFragment.setOnClickListener(this)

        titleSongTextView.isSelected = true
        artistSongTextView.isSelected = true

        if (MusicPlayer.isServiceBound) {
            controlFragment.visibility = View.VISIBLE
            titleSongTextView.text = CurrentPlaylistManager.mSong.title
            artistSongTextView.text = CurrentPlaylistManager.mSong.artistName
            playPauseButton.isPlayed = MusicPlayer.isPlaying()
            playPauseButton.startAnimation()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.preButton -> {
                MusicPlayer.pre()
            }

            R.id.nextButton -> {
                MusicPlayer.next()
            }

            R.id.playPauseButton -> {
                playOrPause()
                playPauseButton.startAnimation()
            }

            R.id.playPauseWrapper -> {
                MusicPlayer.playOrPause()
                playPauseButton.startAnimation()
            }

            R.id.controlFragment -> {
                val intent = Intent(activity, NowPlayingActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

}