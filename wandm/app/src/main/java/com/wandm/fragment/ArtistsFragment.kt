package com.wandm.fragment

import android.os.Bundle
import com.wandm.R
import org.jetbrains.anko.support.v4.toast

class ArtistsFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_artists
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        toast("artist")
    }

}