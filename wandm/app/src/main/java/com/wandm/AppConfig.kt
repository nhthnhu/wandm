package com.wandm

import android.graphics.Color
import com.ms_square.etsyblur.BlurConfig

object AppConfig {

    /**
     * @return BlurConfig to support BlurringView of EtsyBlur
     */
    fun getBlurViewConfig(): BlurConfig {
        return BlurConfig.Builder()
                .radius(22)
                .overlayColor(Color.argb(55, 225, 225, 225))  // semi-transparent white color
                .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                .debug(true)
                .build()
    }
}