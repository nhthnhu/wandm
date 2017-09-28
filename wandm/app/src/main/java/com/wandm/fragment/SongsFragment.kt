package com.wandm.fragment

import android.os.Bundle
import com.wandm.R
import org.jetbrains.anko.support.v4.toast


class SongsFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_songs
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        toast("songs")
    }
}