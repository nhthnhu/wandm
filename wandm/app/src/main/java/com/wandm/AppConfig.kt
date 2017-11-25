package com.wandm

import android.graphics.Color
import com.ms_square.etsyblur.BlurConfig
import com.wandm.utils.PreferencesUtils

object AppConfig {

    val DEBUG = java.lang.Boolean.parseBoolean("true")

    /**
     * @return BlurConfig to support BlurringView of EtsyBlur
     */
    fun getBlurViewConfig(): BlurConfig {
        val isLightTheme = PreferencesUtils.getLightTheme()
        if (isLightTheme)
            return BlurConfig.Builder()
                    .radius(22)
                    .overlayColor(Color.argb(80, 225, 225, 225))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .build()
        else
            return BlurConfig.Builder()
                    .radius(22)
                    .overlayColor(Color.argb(55, 0, 0, 0))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .build()
    }

}