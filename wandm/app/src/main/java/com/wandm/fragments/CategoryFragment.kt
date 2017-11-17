package com.wandm.fragments

import android.os.Bundle
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.wandm.R
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : BaseFragment() {
    override fun getLayoutResId() = R.layout.fragment_category

    override fun onCreatedView(savedInstanceState: Bundle?) {
        setupUI()
    }

    /**
     *  Setup all of UI if permission was granted
     */
    private fun setupUI() {
        val pagerItems = FragmentPagerItems(this.context)
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.songs),
                SongsFragment::class.java))
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.favorite),
                FavoritesFragment::class.java))
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.artists),
                ArtistsFragment::class.java))
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.albums),
                AlbumsFragment::class.java))
        pagerItems.add(FragmentPagerItem.of(resources.getString(R.string.folders),
                FoldersFragment::class.java))

        val fragmentAdapter = FragmentPagerItemAdapter(fragmentManager, pagerItems)
        listViewPagers.adapter = fragmentAdapter
        listTabs.setViewPager(listViewPagers)
    }

}