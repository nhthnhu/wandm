package com.wandm.activities

import android.app.WallpaperManager
import android.os.Bundle
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.wandm.AppConfig
import com.wandm.R
import com.wandm.fragment.AlbumsFragment
import com.wandm.fragment.ArtistsFragment
import com.wandm.fragment.SongsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_list_view_pager.*


class MainActivity : BaseActivity() {

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        blurringView.blurConfig(AppConfig.getBlurViewConfig())

        val pagerItems = FragmentPagerItems(this)

        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.fragment_songs),
                SongsFragment::class.java))
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.fragment_artists),
                ArtistsFragment::class.java))
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.fragment_albums),
                AlbumsFragment::class.java))

        val fragmentAdapter = FragmentPagerItemAdapter(supportFragmentManager, pagerItems)
        listViewPagers.adapter = fragmentAdapter
        listTabs.setViewPager(listViewPagers)
    }

    override fun onResume() {
        super.onResume()
        setBackground()
    }

    /**
     * Set blur background for this Activity
     */
    private fun setBackground() {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        background.background = wallpaperDrawable
        blurringView.blurredView(background)
    }
}
