package com.wandm.activities

import android.app.WallpaperManager
import android.os.Bundle
import com.wandm.AppConfig
import com.wandm.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        blurringView.blurConfig(AppConfig.getBlurViewConfig())
    }

    override fun onResume() {
        super.onResume()
        setBackground()
    }

    /**
     * Set blur background for this Activity
     */
    private fun setBackground() {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        background.background = wallpaperDrawable
        blurringView.blurredView(background)
    }
}
