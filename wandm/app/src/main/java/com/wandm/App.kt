package com.wandm

import android.annotation.SuppressLint
import android.app.Application

class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SmartAsyncPolicyHolder.INSTANCE.init(applicationContext)
    }
}