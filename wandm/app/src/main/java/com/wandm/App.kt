package com.wandm

import android.annotation.SuppressLint
import android.app.Application
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.wandm.permissions.PermissionHelper
import com.wandm.utils.PreferencesUtils
import java.io.IOException
import java.io.InputStream

class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        PermissionHelper.init(this)
        SmartAsyncPolicyHolder.INSTANCE.init(applicationContext)

        val localImageLoaderConfiguration = ImageLoaderConfiguration.Builder(this).imageDownloader(object : BaseImageDownloader(this) {
            internal var prefs = PreferencesUtils.instance

            @Throws(IOException::class)
            override fun getStreamFromNetwork(imageUri: String, extra: Any): InputStream {
                if (prefs.loadArtistImages()) return super.getStreamFromNetwork(imageUri, extra)
                throw IOException()
            }
        }).build()
        ImageLoader.getInstance().init(localImageLoaderConfiguration)
    }


}