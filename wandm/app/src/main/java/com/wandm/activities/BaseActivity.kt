package com.wandm.activities

import android.app.WallpaperManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.view.WindowManager
import com.ms_square.etsyblur.BlurringView

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Overwritten in subclasses
     *
     * @return layout ID
     */
    protected abstract fun getLayoutResId(): Int

    /**
     * initView is called in onCreate()
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        setup()
        initView(savedInstanceState)
    }

    /**
     *  Used to setup screen and window
     */
    private fun setup() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     *  Used to add fragment
     *
     *  @param fragment is fragment that is added
     *  @param containerViewId is fragment's ID
     *  @param tag is fragment's TAG
     */
    fun addFragment(fragment: Fragment, containerViewId: Int, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(containerViewId, fragment, tag)
                .commit()

    }

    /**
     * Set blur background for this Activity
     *
     * @param imageView
     * @param blurringView
     */
    protected fun setBlurBackground(imageView: AppCompatImageView, blurringView: BlurringView) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        imageView.background = wallpaperDrawable
        blurringView.blurredView(imageView)
    }
}