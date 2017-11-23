package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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

class ArtistsFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_artists
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setArtistAlbumSortOrder(SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z)
        artistsRecyclerView.layoutManager = LinearLayoutManager(activity)
        artistsFastScroller.setRecyclerView(artistsRecyclerView)

        if (activity != null) {
            doAsync {
                val artists = ArtistLoader.getAllArtists(App.instance) as ArrayList<Artist>
                val adapter = ArtistsAdapter(artists)

                uiThread {
                    artistsRecyclerView.adapter = adapter
                    setItemDecoration()
                    artistsRecyclerView.adapter.notifyDataSetChanged()

                    if (artists.size > 0)
                        ArtistLoader.getAllArtists(App.instance) as ArrayList<Artist>

                    artistsProgressBar.visibility = View.GONE
                    adapter.setOnItemClickListener { artist, _ ->
                        val fragmentManager = MainActivity.instance.supportFragmentManager
                        val dialogFragment = ArtistDetailDialog.newInstance(artist.id)
                        dialogFragment.show(fragmentManager, "ArtistDetailDialog")
                    }
                }
            }
        }

    }

    private fun setItemDecoration() {
        artistsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

}