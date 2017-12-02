package com.wandm.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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

class AlbumsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private val TAG = "AlbumsFragment"
    private var adapter: AlbumsAdapter? = null
    override fun getLayoutResId() = R.layout.fragment_albums

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z)
        albumsRecyclerView.layoutManager = LinearLayoutManager(activity)
        albumsFastScroller.setRecyclerView(albumsRecyclerView)
        container.setOnRefreshListener(this)

        adapter = AlbumsAdapter(ArrayList())
        albumsRecyclerView.adapter = adapter
        setItemDecoration()


        if (activity != null) {
            loadAlbums()
        }
    }

    override fun onRefresh() {
        if (activity != null) {
            container.isRefreshing = true
            loadAlbums()
        }
    }

    private fun loadAlbums() {
        doAsync {
            val albums = AlbumLoader.getAllAlbums(App.instance) as ArrayList<Album>


            uiThread {
                try {
                    container.isRefreshing = false
                    adapter?.mListAlbums = albums
                    albumsRecyclerView.adapter.notifyDataSetChanged()

                    if (albums.size > 0)
                        albumsFastScroller.visibility = View.VISIBLE


                    albumsProgressBar.visibility = View.GONE

                    adapter?.setOnItemClickListener { album, position ->
                        val fragmentManager = MainActivity.instance.supportFragmentManager
                        val dialogFragment = AlbumDetailDialog.newInstance(album.id)
                        dialogFragment.show(fragmentManager, "AlbumDetailDialog")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }
        }
    }

    private fun setItemDecoration() {
        albumsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }


}