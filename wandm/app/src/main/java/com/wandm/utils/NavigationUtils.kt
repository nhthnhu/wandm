package com.wandm.utils

import android.support.v7.app.AppCompatActivity
import com.wandm.R
import com.wandm.dialogs.ArtistDetailFragment

object NavigationUtils {
    fun goToArtistDetail(activity: AppCompatActivity, artistId: Long) {
        val fragmentManager = activity.supportFragmentManager
        val fragment = ArtistDetailFragment.newInstance(artistId)
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}