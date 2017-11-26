package com.wandm.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.wandm.App
import com.wandm.data.PlaybackStatus
import com.wandm.events.MessageEvent
import com.wandm.events.MusicEvent
import com.wandm.utils.Constants
import org.greenrobot.eventbus.EventBus

class WMReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(p1?.getAction())) {
            val intentNoisy = Intent(Constants.ACTION_BECOMING_NOISY)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intentNoisy)
        }
    }

}