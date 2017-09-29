package com.wandm.activities

import android.os.Bundle
import com.wandm.R
import kotlinx.android.synthetic.main.activity_now_playing.*

class NowPlayingActivity : BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_now_playing
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        setBackground(nowPlayingBackground, nowPlayingBlurringView)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbarNowPlaying)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
        }
    }

}