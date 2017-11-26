package com.wandm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SlidingPaneLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.wandm.App
import com.wandm.R
import com.wandm.adapters.SongsAdapter
import com.wandm.data.CurrentPlaylistManager
import com.wandm.database.FavoritesTable
import com.wandm.database.MusicDBHandler
import com.wandm.dialogs.AlarmDialog
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.services.DownloadService
import com.wandm.services.MusicPlayer
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.activity_now_playing.*
import kotlinx.android.synthetic.main.content_now_playing.*
import kotlinx.android.synthetic.main.sliding_pane_songs.*
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColor
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

    private var colorResId = R.color.color_dark_theme
    private var colorResIdPressed = R.color.color_light_theme
    private var textSize = 18

    companion object {
        lateinit var instance: NowPlayingActivity
    }

    // Listening events of SlidingPaneLayout
    private val panelListener = object : SlidingPaneLayout.PanelSlideListener {

        override fun onPanelClosed(arg0: View) {


        }

        override fun onPanelOpened(arg0: View) {


        }

        override fun onPanelSlide(arg0: View, arg1: Float) {


        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        when (event.message) {
            MusicEvent.PREPARED_ACTION -> {
                titleSongTextView.text = CurrentPlaylistManager.currentSong?.title
                artistSongTextView.text = CurrentPlaylistManager.currentSong?.artistName
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

            MusicEvent.ALARM_OFF -> {
                setTimer(true)
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_now_playing

    }

    override fun initView(savedInstanceState: Bundle?) {
        instance = this
        setupToolbar()
        songBlurringView.blurConfig(Utils.getBlurViewConfig())
        setTheme()

        EventBus.getDefault().register(this)

        setShuffleMode(true)
        setRepeatMode(true)
        setFavorite(true)
        setDownload(true)
        setAlbumArt()
        setTimer(true)

        playpauseButton.setOnClickListener(this)
        playpauseWrapper.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        preButton.setOnClickListener(this)
        shuffleButton.setOnClickListener(this)
        repeatButton.setOnClickListener(this)
        favoriteButton.setOnClickListener(this)
        downloadButton.setOnClickListener(this)
        songMenuButton.setOnClickListener(this)
        setAlarmButton.setOnClickListener(this)

        artistSongTextView.text = CurrentPlaylistManager.currentSong?.artistName
        titleSongTextView.text = CurrentPlaylistManager.currentSong?.title
        artistSongTextView.isSelected = true
        titleSongTextView.isSelected = true

        mHandler = Handler()

        if (MusicPlayer.isPlaying()) {
            playpauseButton.isPlayed = true
            albumImage.start()
        } else {
            playpauseButton.isPlayed = false
            albumImage.stop()
        }

        playpauseButton.startAnimation()
        preparedSeekBar()
    }

    override fun onResume() {
        super.onResume()
        setupUI()
        setBlurBackground(songBackground, songBlurringView)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /**
     * Used to setup toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbarNowPlaying)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }


    private fun setupUI() {
        songSlidingPane.setPanelSlideListener(panelListener)
        songSlidingPane.parallaxDistance = 100
        songSlidingPane.sliderFadeColor = ContextCompat.getColor(this, android.R.color.transparent)

        songRecyclerView.layoutManager = LinearLayoutManager(this)
        songRecyclerView.adapter = SongsAdapter(CurrentPlaylistManager.listSongs, false) { song, position, action ->
            when (action) {
                SongsAdapter.ACTION_ADD_PLAYLIST -> {
                    Toast.makeText(this, "Add to playlist", Toast.LENGTH_SHORT).show()
                }

                SongsAdapter.ACTION_PLAY -> {
                    MusicPlayer.bind(null)
                    songSlidingPane.closePane()
                }

                SongsAdapter.ACTION_ADD_FAVORITES -> {
                    Toast.makeText(this, "Add to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val mUpdateSongTime = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            if (mServiceBound) {
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
        MusicPlayer.playOrPause()
        playpauseButton.startAnimation()
        mServiceBound = false
        songProgress.progress = 0
        songElapsedTime.text = "00:00"
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

    @SuppressLint("SetTextI18n")
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

            R.id.songMenuButton -> {

            }

            R.id.setAlarmButton -> {
                setTimer(false)
            }
        }
    }

    private fun setRepeatMode(init: Boolean) {
        repeatMode = PreferencesUtils.getRepeatMode()

        if (init) {
            when (repeatMode) {
                0 -> {
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                    repeatButton.setColorResource(colorResId)
                }
                1 -> {
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                    repeatButton.setColorResource(colorResIdPressed)
                }
                else -> {
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
                    repeatButton.setColorResource(colorResIdPressed)
                }
            }

        } else {
            when (repeatMode) {
                0 -> {
                    repeatMode++
                    repeatButton.setColorResource(colorResIdPressed)
                    PreferencesUtils.setRepeatMode(repeatMode)
                }
                1 -> {
                    repeatMode++
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE)
                    repeatButton.setColorResource(colorResIdPressed)
                    PreferencesUtils.setRepeatMode(repeatMode)
                }
                else -> {
                    repeatMode = 0
                    repeatButton.setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                    repeatButton.setColorResource(colorResId)
                    PreferencesUtils.setRepeatMode(repeatMode)
                }
            }
        }
    }

    private fun setShuffleMode(init: Boolean) {
        isShuffle = PreferencesUtils.getShuffleMode()

        if (init) {
            if (isShuffle)
                shuffleButton.setColorResource(colorResIdPressed)
            else
                shuffleButton.setColorResource(colorResId)
        } else {

            if (isShuffle) {
                isShuffle = false
                shuffleButton.setColorResource(colorResId)
                PreferencesUtils.setShuffleMode(isShuffle)
            } else {
                isShuffle = true
                shuffleButton.setColorResource(colorResIdPressed)
                PreferencesUtils.setShuffleMode(isShuffle)
            }
        }
    }

    private fun setFavorite(init: Boolean) {
        val song = MusicDBHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                getFavoriteSong(CurrentPlaylistManager.currentSong!!.data)
        isFavorite = song != null

        if (init) {
            if (isFavorite) {
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART)
                favoriteButton.setColorResource(R.color.color_red)
            } else {
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART_OUTLINE)
                favoriteButton.setColorResource(colorResId)
            }
        } else {
            if (isFavorite) {
                isFavorite = false
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART_OUTLINE)
                favoriteButton.setColorResource(colorResId)
                MusicDBHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                        remove(CurrentPlaylistManager.currentSong!!)
            } else {
                isFavorite = true
                favoriteButton.setIcon(MaterialDrawableBuilder.IconValue.HEART)
                favoriteButton.setColorResource(R.color.color_red)
                MusicDBHandler.getInstance(App.instance, FavoritesTable.TABLE_NAME)?.
                        insert(CurrentPlaylistManager.currentSong!!)
            }
        }
    }

    private fun setAlbumArt() {
        val uri: String

        if (CurrentPlaylistManager.currentSong?.albumId == (-1).toLong()) {
            uri = CurrentPlaylistManager.currentSong!!.albumArt
        } else
            uri = Utils.getAlbumArtUri(CurrentPlaylistManager.currentSong!!.albumId).toString()

        doAsync {
            albumImage.setCoverURL(uri)
        }
    }

    private fun setDownload(init: Boolean) {
        if (init) {
            if (!CurrentPlaylistManager.currentSong?.downloadEnable!!) {
                downloadButton.setColor(resources.getColor(colorResIdPressed))
                downloadButton.isEnabled = false
                isDownloaded = true
            } else {
                downloadButton.setColor(resources.getColor(colorResId))
                downloadButton.isEnabled = true
                isDownloaded = false
            }
        } else {
            val intent = Intent(this, DownloadService::class.java)
            intent.putExtra(DownloadService.FILE_NAME, CurrentPlaylistManager.currentSong?.title)
            intent.putExtra(DownloadService.URL_PATH, CurrentPlaylistManager.currentSong?.data)
            startService(intent)
        }
    }

    private fun setTimer(init: Boolean) {
        val timeStr = PreferencesUtils.getAlarm()
        if (init) {
            if (timeStr.equals("0;;0")) {
                setAlarmButton.setColorResource(colorResId)
            } else
                setAlarmButton.setColorResource(colorResIdPressed)
        } else {
            if (timeStr.equals("0;;0")) {
                val fragmentManager = supportFragmentManager
                val dialogFragment = AlarmDialog.newInstance { isSetTimer ->
                    if (isSetTimer)
                        setAlarmButton.setColorResource(colorResIdPressed)
                    else
                        setAlarmButton.setColorResource(colorResId)
                }
                dialogFragment.show(fragmentManager, "AlarmDialog")
            } else {
                PreferencesUtils.setAlarm("0;;0")
                setAlarmButton.setColorResource(colorResId)
                Toast.makeText(this, resources.getString(R.string.turn_off_alarm), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setTheme() {
        val isLightTheme = PreferencesUtils.getLightTheme()
        Utils.applyLightTheme(this)
        textSize = Utils.getTextSize()

        if (isLightTheme) {
            colorResId = R.color.color_light_theme
            colorResIdPressed = R.color.pressed_color
        }

        titleSongTextView.textColor = resources.getColor(colorResId)
        titleSongTextView.textSize = textSize.toFloat()
        artistSongTextView.textColor = resources.getColor(colorResId)
        artistSongTextView.textSize = (textSize - 2).toFloat()
        songElapsedTime.textColor = resources.getColor(colorResId)
        songElapsedTime.textSize = (textSize - 2).toFloat()
        songDuration.textColor = resources.getColor(colorResId)
        songDuration.textSize = (textSize - 2).toFloat()
        songProgress.getProgressDrawable().setColorFilter(resources.getColor(colorResId)
                , PorterDuff.Mode.SRC_IN)
        songProgress.getThumb().setColorFilter(resources.getColor(colorResId)
                , PorterDuff.Mode.SRC_IN)

        playpauseButton.setColor(resources.getColor(colorResId))
        preButton.setColor(resources.getColor(colorResId))
        nextButton.setColor(resources.getColor(colorResId))
        repeatButton.setColor(resources.getColor(colorResId))
        shuffleButton.setColor(resources.getColor(colorResId))
        favoriteButton.setColor(resources.getColor(colorResId))
        downloadButton.setColor(resources.getColor(colorResId))
        songMenuButton.setColor(resources.getColor(colorResId))
        setAlarmButton.setColor(resources.getColor(colorResId))

    }

}