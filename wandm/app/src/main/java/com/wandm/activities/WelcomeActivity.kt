package com.wandm.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.wandm.AppConfig
import com.wandm.R
import com.wandm.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_welcome.*
import org.jetbrains.anko.textColor

class WelcomeActivity : BaseActivity() {
    override fun getLayoutResId() = R.layout.activity_welcome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blurringView.blurConfig(AppConfig.getBlurViewConfig())
    }

    override fun initView(savedInstanceState: Bundle?) {
        instance = this
        setBlurBackground(background, blurringView)
        val isLightTheme = PreferencesUtils.getLightTheme()
        var colorResId = R.color.color_dark_theme
        if (isLightTheme) {
            colorResId = R.color.color_light_theme
        }

        labelWelcome.textColor = resources.getColor(colorResId)
        labelAppName.textColor = resources.getColor(colorResId)

        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                val intent = Intent(instance, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }

            override fun onAnimationStart(p0: Animation?) {
                welcomeView.visibility = View.VISIBLE
            }

        })
        welcomeView.animation = animation
        welcomeView.animation.start()
    }

    companion object {
        private var instance: WelcomeActivity? = null
    }
}
