package com.wandm.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.adapters.ArtistsAdapter
import com.wandm.dialogs.ArtistDetailDialog
import com.wandm.loaders.ArtistLoader
import com.wandm.models.Artist
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_artists.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArtistsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private val TAG = "ArtistsFragment"
    private var adapter: ArtistsAdapter? = null

    override fun getLayoutResId() = R.layout.fragment_artists

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setArtistAlbumSortOrder(SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z)
        artistsRecyclerView.layoutManager = LinearLayoutManager(activity)
        artistsFastScroller.setRecyclerView(artistsRecyclerView)
        container.setOnRefreshListener(this)

        adapter = ArtistsAdapter(ArrayList())
        artistsRecyclerView.adapter = adapter
        setItemDecoration()

        if (activity != null) {
            loadArtists()
        }

    }

    override fun onRefresh() {
        if (activity != null) {
            container.isRefreshing = true
            loadArtists()
        }
    }

    private fun loadArtists() {
        doAsync {
            val artists = ArtistLoader.getAllArtists(App.instance) as ArrayList<Artist>

            uiThread {
                try {
                    container.isRefreshing = false
                    adapter?.mListArtists = artists
                    artistsRecyclerView.adapter.notifyDataSetChanged()

                    if (artists.size > 0) {
                        ArtistLoader.getAllArtists(App.instance) as ArrayList<Artist>
                        artistsFastScroller.visibility = View.VISIBLE
                    }

                    artistsProgressBar.visibility = View.GONE

                    adapter?.setOnItemClickListener { artist, _ ->
                        val fragmentManager = MainActivity.instance.supportFragmentManager
                        val dialogFragment = ArtistDetailDialog.newInstance(artist.id)
                        dialogFragment.show(fragmentManager, "ArtistDetailDialog")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }
        }
    }

    private fun setItemDecoration() {
        artistsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

}