package com.wandm.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wandm.IWMService

class WMService : Service() {
    private val mBinder = ServiceStub()
    private val mPlayer = MultiPlayer()

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    inner class ServiceStub : IWMService.Stub() {
        override fun playNext() {
            mPlayer.next()
        }

        override fun playPre() {
            mPlayer.pre()
        }

        override fun stop() {
            mPlayer.stop()
        }

        override fun pause() {
            mPlayer.pause()
        }

        override fun play() {
            mPlayer.pause()
        }

        override fun resume() {
            mPlayer.resume()
        }

        override fun isPlaying(): Boolean {
            return mPlayer.isPlaying()
        }

    }

}