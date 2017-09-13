package com.wandm.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Overwritten in subclasses
     *
     * @return layout ID
     */
    protected abstract fun getLayoutResId(): Int

    /**
     * initView is called in onCreate()
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        initView(savedInstanceState)
    }

    protected fun addFragment(fragment: Fragment, containerViewId: Int, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .add(containerViewId, fragment, tag)
                .commit()

    }
}