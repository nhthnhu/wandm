package com.wandm.data

import android.os.Handler
import com.wandm.services.MultiPlayer

class SeekbarManager {
    private var myHandler = Handler()
    private var mPlayer = MultiPlayer()

    private var duration = mPlayer.duration()
    private var startTime = 0
    private var finalTime = 0

    private var mServiceBound: Boolean? = true
}
