package com.wandm.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.wandm.App
import com.wandm.R
import com.wandm.utils.PreferencesUtils
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : BaseFragment() {
    private var pagerItems: FragmentPagerItems? = null

    companion object {
        var instance: CategoryFragment? = null
    }

    override fun getLayoutResId() = R.layout.fragment_category

    override fun onCreatedView(savedInstanceState: Bundle?) {
        instance = this
        setupUI()
    }

    /**
     *  Setup all of UI if permission was granted
     */
    private fun setupUI() {
        pagerItems = FragmentPagerItems(this.context)
        pagerItems?.add(FragmentPagerItem.of(resources.getString(R.string.songs),
                SongsFragment::class.java))
        pagerItems?.add(FragmentPagerItem.of(resources.getString(R.string.favorites),
                FavoritesFragment::class.java))
        pagerItems?.add(FragmentPagerItem.of(resources.getString(R.string.artists),
                ArtistsFragment::class.java))
        pagerItems?.add(FragmentPagerItem.of(resources.getString(R.string.albums),
                AlbumsFragment::class.java))
        pagerItems?.add(FragmentPagerItem.of(resources.getString(R.string.folders),
                FoldersFragment::class.java))

        val fragmentAdapter = FragmentPagerItemAdapter(fragmentManager, pagerItems)
        listViewPagers.adapter = fragmentAdapter
        listTabs.setViewPager(listViewPagers)

        var colorResId = R.color.color_dark_theme
        if (PreferencesUtils.getLightTheme())
            colorResId = R.color.color_light_theme

        listTabs.setSelectedIndicatorColors(activity.resources.getColor(colorResId))
    }

}