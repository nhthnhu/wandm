package com.wandm.activities

import android.Manifest
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SlidingPaneLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.wandm.R
import com.wandm.adapters.MenuAdapter
import com.wandm.fragments.*
import com.wandm.permissions.PermissionCallback
import com.wandm.permissions.PermissionHelper
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_list_view_pagers.*
import kotlinx.android.synthetic.main.sliding_pane.*


class MainActivity : BaseActivity() {

    companion object {
        lateinit var instance: MainActivity
    }

    private val panelListener = object : SlidingPaneLayout.PanelSlideListener {

        override fun onPanelClosed(arg0: View) {


        }

        override fun onPanelOpened(arg0: View) {


        }

        override fun onPanelSlide(arg0: View, arg1: Float) {


        }

    }
    private val permissionReadStorageCallback = object : PermissionCallback {
        override fun permissionGranted() {
            setupUI()
        }

        override fun permissionRefused() {
            finish()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupToolbar()
        instance = this
        // blurringView.blurConfig(AppConfig.getBlurViewConfig())
        addFragment(QuickControlFragment(), R.id.controlFragmentContainer, "QuickControlFragment")
    }

    override fun onResume() {
        super.onResume()
        setBlurBackground(background, blurringView)
        if (Utils.isMarshmallow())
            checkPermissionReadStorage()
        else setupUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_item_menu -> {
                val dialog = SearchDialogFragment()
                dialog.show(supportFragmentManager, "SearchDialogFragment")
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Used to setup toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupUI() {
        val pagerItems = FragmentPagerItems(this)
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

        val fragmentAdapter = FragmentPagerItemAdapter(supportFragmentManager, pagerItems)
        listViewPagers.adapter = fragmentAdapter
        listTabs.setViewPager(listViewPagers)

        slidingPane.setPanelSlideListener(panelListener)
        slidingPane.parallaxDistance = 100
        slidingPane.sliderFadeColor = ContextCompat.getColor(this, android.R.color.transparent)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MenuAdapter()

    }

    private fun checkPermissionReadStorage() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (PermissionHelper.checkPermission(permission)) {
            setupUI()
        } else {
            if (PermissionHelper.shouldShowRequestPermissionRationale(this, permission)) {
                Snackbar.make(slidingPane, getString(R.string.request_read_storage_permission), Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok) {
                            PermissionHelper.askForPermission(this,
                                    permission,
                                    permissionReadStorageCallback)
                        }.show()
            } else {
                PermissionHelper.askForPermission(this,
                        permission,
                        permissionReadStorageCallback)
            }
        }
    }

}
