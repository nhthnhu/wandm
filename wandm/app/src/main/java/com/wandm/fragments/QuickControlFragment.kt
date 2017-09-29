package com.wandm.fragments

import android.os.Bundle
import android.view.View
import com.wandm.R
import kotlinx.android.synthetic.main.fragment_quick_control.*

class QuickControlFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_quick_control
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        controlFragment.visibility = View.VISIBLE
    }

}