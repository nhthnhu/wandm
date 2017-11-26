package com.wandm.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SlidingPaneLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.adapters.MenuAdapter
import com.wandm.data.CurrentPlaylistManager
import com.wandm.fragments.CategoryFragment
import com.wandm.fragments.QuickControlFragment
import com.wandm.permissions.PermissionCallback
import com.wandm.permissions.PermissionHelper
import com.wandm.speech.Speech
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.sliding_pane_main.*
import org.jetbrains.anko.textColor


class MainActivity : BaseActivity() {

    companion object {
        private val TAG = "MainActivity"
        lateinit var instance: MainActivity
    }

    private var isSetup = false
    private var itemPagerPosition = 0
    private var colorResId = R.color.color_dark_theme
    private var textSize = 18

    // Listening events of SlidingPaneLayout
    private val panelListener = object : SlidingPaneLayout.PanelSlideListener {

        override fun onPanelClosed(arg0: View) {
            CategoryFragment.instance?.listViewPagers?.currentItem = itemPagerPosition
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

    override fun getLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blurringView.blurConfig(Utils.getBlurViewConfig())
    }

    override fun initView(savedInstanceState: Bundle?) {
        instance = this
        setupToolbar()
        Speech.init(this)
        addFragment(QuickControlFragment(), R.id.quick_control_container, "QuickControlFragment")
    }

    override fun onResume() {
        super.onResume()
        setTheme()
        setBlurBackground(background, blurringView)
        if (Utils.isMarshmallow())
            checkPermissionReadStorage()
        else setupUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val itemSearch = menu?.findItem(R.id.search_item_menu)

        if (PreferencesUtils.getLightTheme())
            itemSearch?.setIcon(R.drawable.ic_action_search_light)
        else
            itemSearch?.setIcon(R.drawable.ic_action_search_dark)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_item_menu -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
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
        setSupportActionBar(toolbarSettings)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true)

            if (PreferencesUtils.getLightTheme())
                actionBar.setTitle(Html.fromHtml("<font color='#333232'>We & Music</font>"))
            else
                actionBar.setTitle(Html.fromHtml("<font color='#ece8e8'>We & Music</font>"))
        }
    }

    /**
     *  Setup all of UI if permission was granted
     */
    private fun setupUI() {
        if (isSetup) return

        isSetup = true
        slidingPane.setPanelSlideListener(panelListener)
        slidingPane.parallaxDistance = 100
        slidingPane.sliderFadeColor = ContextCompat.getColor(this, android.R.color.transparent)

        val song = CurrentPlaylistManager.currentSong
        if (song != null) {
            Picasso.with(this)
                    .load(Utils.getAlbumArtUri(song.albumId).toString())
                    .into(albumImageView, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            albumImageView.background = getDrawable(R.drawable.ic_music)
                        }
                    })

            songTitleTextView.text = song.title
        } else {
            albumImageView.background = getDrawable(R.drawable.ic_music)
            songTitleTextView.text = resources.getString(R.string.app_name)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter()
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { position ->
            itemPagerPosition = position
            slidingPane.closePane()
        }

        settingButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


        addFragment(CategoryFragment(), R.id.fragment_container, CategoryFragment::class.java.name)
    }

    /**
     * Checking PermissionReadStorage
     */
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

    private fun setTheme() {
        val isLightTheme = PreferencesUtils.getLightTheme()
        textSize = Utils.getTextSize()

        Utils.applyLightTheme(this)

        colorResId = R.color.color_dark_theme
        if (isLightTheme) {
            colorResId = R.color.color_light_theme
        }

        settingsButton.setColor(resources.getColor(colorResId))
        settingsTextView.textColor = resources.getColor(colorResId)
        settingsTextView.textSize = textSize.toFloat()
        songTitleTextView.textColor = resources.getColor(colorResId)
        songTitleTextView.textSize = (textSize - 2).toFloat()

    }
}
