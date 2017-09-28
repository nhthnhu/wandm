package com.wandm.fragment

import android.os.Bundle
import com.wandm.R
import org.jetbrains.anko.support.v4.toast

class AlbumsFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_albums
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        toast("albums")
    }


}