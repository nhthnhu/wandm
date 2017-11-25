package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.adapters.AlbumsAdapter
import com.wandm.dialogs.AlbumDetailDialog
import com.wandm.loaders.AlbumLoader
import com.wandm.models.Album
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_albums.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AlbumsFragment : BaseFragment() {
    override fun getLayoutResId() = R.layout.fragment_albums

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z)
        albumsRecyclerView.layoutManager = LinearLayoutManager(activity)
        albumsFastScroller.setRecyclerView(albumsRecyclerView)


        if (activity != null) {
            doAsync {
                val albums = AlbumLoader.getAllAlbums(App.instance) as ArrayList<Album>

                val adapter = AlbumsAdapter(albums)
                uiThread {
                    albumsRecyclerView.adapter = adapter
                    setItemDecoration()
                    albumsRecyclerView.adapter.notifyDataSetChanged()

                    if (albums.size > 0)
                        albumsFastScroller.visibility = View.VISIBLE


                    albumsProgressBar.visibility = View.GONE

                    adapter.setOnItemClickListener { album, position ->
                        val fragmentManager = MainActivity.instance.supportFragmentManager
                        val dialogFragment = AlbumDetailDialog.newInstance(album.id)
                        dialogFragment.show(fragmentManager, "AlbumDetailDialog")
                    }
                }
            }
        }
    }

    private fun setItemDecoration() {
        albumsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }


}