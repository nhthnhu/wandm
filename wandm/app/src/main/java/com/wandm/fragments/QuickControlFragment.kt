package com.wandm.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.activities.NowPlayingActivity
import com.wandm.data.CurrentPlaylistManager
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.services.MusicPlayer
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.fragment_quick_control.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class QuickControlFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "QuickControlFragment"

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                controlFragment.visibility = View.VISIBLE
                titleSongTextView.text = CurrentPlaylistManager.currentSong?.title
                artistSongTextView.text = CurrentPlaylistManager.currentSong?.artistName
                setAlbumArt()
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

            MusicEvent.COMPLETED_ACTION -> {
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
        EventBus.getDefault().register(this)

        nextButton.setOnClickListener(this)
        preButton.setOnClickListener(this)
        playPauseButton.setOnClickListener(this)
        playPauseWrapper.setOnClickListener(this)
        controlFragment.setOnClickListener(this)

        titleSongTextView.isSelected = true
        artistSongTextView.isSelected = true

        if (MusicPlayer.isServiceBound) {
            controlFragment.visibility = View.VISIBLE
            titleSongTextView.text = CurrentPlaylistManager.currentSong?.title
            artistSongTextView.text = CurrentPlaylistManager.currentSong?.artistName
            playPauseButton.isPlayed = MusicPlayer.isPlaying()
            playPauseButton.startAnimation()
            setAlbumArt()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
                MusicPlayer.playOrPause()
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

    private fun setAlbumArt() {
        var uri = ""

        if (CurrentPlaylistManager.currentSong?.albumId == -1.toLong())
            uri = CurrentPlaylistManager.currentSong!!.albumArt
        else
            uri = Utils.getAlbumArtUri(CurrentPlaylistManager.currentSong!!.albumId).toString()

        if (uri.isEmpty()) {
            albumImageView.background = context.getDrawable(R.drawable.ic_action_music)
            return
        }

        Picasso.with(context)
                .load(uri)
                .into(albumImageView, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        albumImageView.background = context.getDrawable(R.drawable.ic_action_music)
                    }
                })
    }

}