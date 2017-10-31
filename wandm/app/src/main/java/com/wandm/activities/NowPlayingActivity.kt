package com.wandm.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import com.wandm.App
import com.wandm.R
import com.wandm.data.CurrentPlaylistManager
import com.wandm.database.FavoritesTable
import com.wandm.database.SongsBaseHandler
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.services.DownloadService
import com.wandm.services.MusicPlayer
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.activity_now_playing.*
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
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
    private var repeatMode = 0
    private var isFavorite = false
    private var isDownloaded = false


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                titleSongTextView.text = CurrentPlaylistManager.mSong.title
                artistSongTextView.text = CurrentPlaylistManager.mSong.artistName
                preparedSeekBar()
                albumImage.start()
                setFavorite(true)
                setAlbumArt()
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

            MusicEvent.COMPLETED_ACTION -> {
                playpauseButton.isPlayed = false
                playpauseButton.startAnimation()
                albumImage.stop()
            }

            MusicEvent.REMOVE_NOTI_ACTION -> {
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

        setShuffleMode(true)
        setRepeatMode(true)
        setFavorite(true)
        setDownload(true)
        setAlbumArt()

        playpauseButton.setOnClickListener(this)
        playpauseWrapper.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        preButton.setOnClickListener(this)
        shuffleButton.setOnClickListener(this)
        repeatButton.setOnClickListener(this)
        favoriteButton.setOnClickListener(this)
        downloadButton.setOnClickListener(this)

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


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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
                setShuffleMode(false)
            }

            R.id.repeatButton -> {
                setRepeatMode(false)
            }

            R.id.favoriteButton -> {
                setFavorite(false)
            }

            R.id.downloadButton -> {
                setDownload(false)
            }
        }
    }

    private fun setRepeatMode(init: Boolean) {
        repeatMode = PreferencesUtils.getRepeatMode()

        if (init) {
            if (repeatMode == 0) {
                repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                repeatButton.setColorResource(R.color.color_white)
            } else if (repeatMode == 1) {
                repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                repeatButton.setColorResource(R.color.color_primary_dark)
            } else {
                repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
                repeatButton.setColorResource(R.color.color_primary_dark)
            }

        } else {
            if (repeatMode == 0) {
                repeatMode++
                repeatButton.setColorResource(R.color.color_primary_dark)
                PreferencesUtils.setRepeatMode(repeatMode)
            } else if (repeatMode == 1) {
                repeatMode++
                repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
                repeatButton.setColorResource(R.color.color_primary_dark)
                PreferencesUtils.setRepeatMode(repeatMode)
            } else {
                repeatMode = 0
                repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                repeatButton.setColorResource(R.color.color_white)
                PreferencesUtils.setRepeatMode(repeatMode)
            }
        }
    }

    private fun setShuffleMode(init: Boolean) {
        isShuffle = PreferencesUtils.getShuffleMode()

        if (init) {
            if (isShuffle)
                shuffleButton.setColorResource(R.color.color_primary_dark)
            else
                shuffleButton.setColorResource(R.color.color_white)
        } else {

            if (isShuffle) {
                isShuffle = false
                shuffleButton.setColorResource(R.color.color_white)
                PreferencesUtils.setShuffleMode(isShuffle)
            } else {
                isShuffle = true
                shuffleButton.setColorResource(R.color.color_primary_dark)
                PreferencesUtils.setShuffleMode(isShuffle)
            }
        }
    }

    private fun setFavorite(init: Boolean) {
        val song = SongsBaseHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                getSong(CurrentPlaylistManager.mSong.data)
        if (song == null)
            isFavorite = false
        else
            isFavorite = true

        if (init) {
            if (isFavorite) {
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART)
                favoriteButton.setColorResource(R.color.color_red)
            } else {
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART_OUTLINE)
                favoriteButton.setColorResource(R.color.color_white)
            }
        } else {
            if (isFavorite) {
                isFavorite = false
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART_OUTLINE)
                favoriteButton.setColorResource(R.color.color_white)
                SongsBaseHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                        removeSong(CurrentPlaylistManager.mSong)
            } else {
                isFavorite = true
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART)
                favoriteButton.setColorResource(R.color.color_red)
                SongsBaseHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                        addSong(CurrentPlaylistManager.mSong)
            }
        }
    }

    private fun setAlbumArt() {
        var uri = ""

        if (CurrentPlaylistManager.mSong.albumId == -1.toLong())
            uri = CurrentPlaylistManager.mSong.albumArt
        else
            uri = Utils.getAlbumArtUri(CurrentPlaylistManager.mSong.albumId).toString()

        doAsync {
            albumImage.setCoverURL(uri)
        }
    }

    private fun setDownload(init: Boolean) {
        if (init) {
            if (CurrentPlaylistManager.mSong.albumId != -1.toLong()) {
                downloadButton.setColorResource(R.color.color_primary_dark)
                downloadButton.isEnabled = false
                isDownloaded = true
            } else {
                downloadButton.setColorResource(R.color.color_white)
                downloadButton.isEnabled = true
                isDownloaded = false
            }
        } else {
            val intent = Intent(this, DownloadService::class.java)
            intent.putExtra(DownloadService.FILE_NAME, CurrentPlaylistManager.mSong.title + ".mp3")
            intent.putExtra(DownloadService.URL_PATH, CurrentPlaylistManager.mSong.data)
            startService(intent)
        }
    }

}