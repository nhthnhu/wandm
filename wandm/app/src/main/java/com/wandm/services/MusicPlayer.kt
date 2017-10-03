package com.wandm.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.wandm.App
import com.wandm.IWMService

object MusicPlayer {
    private var mService: IWMService? = null
    private var mCallback: Callback? = null

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mService = IWMService.Stub.asInterface(p1)
            mCallback?.onServiceConnected()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mService = null
            mCallback?.onServiceDisconnected()
        }
    }

    fun bind(callback: Callback) {
        val intent = Intent(App.instance, WMService::class.java)
        intent.action = IWMService::class.java.name
        App.instance.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        mCallback = callback
    }

    fun unbind(context: Context) {
        context.unbindService(mConnection)
    }

    fun next() {
        mService?.playNext()
    }

    fun pre() {
        mService?.playPre()
    }

    fun playOrPause() {
        if (mService != null) {
            if (mService!!.isPlaying)
                mService!!.pause()
            else
                mService!!.resume()
        }
    }

    interface Callback {
        fun onServiceConnected()
        fun onServiceDisconnected()
    }
}
