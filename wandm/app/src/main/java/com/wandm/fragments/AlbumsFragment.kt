package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wandm.App
import com.wandm.R
import com.wandm.adapters.AlbumsAdapter
import com.wandm.loaders.AlbumLoader
import com.wandm.models.Album
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_albums.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.uiThread
import org.jetbrains.anko.uiThread

class AlbumsFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_albums
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z)
        albumsRecyclerView.layoutManager = LinearLayoutManager(activity)
        albumsFastScroller.setRecyclerView(albumsRecyclerView)



        if (activity != null){
            doAsync {
                val albums = AlbumLoader.getAllAlbums(App.instance) as ArrayList<Album>

                val adapter = AlbumsAdapter(albums)
                uiThread {
                    albumsRecyclerView.adapter = adapter
                    setItemDecoration()
                    albumsRecyclerView.adapter.notifyDataSetChanged()
                    albumsFastScroller.visibility = View.VISIBLE;
                    albumsProgressBar.visibility = View.GONE;
                }
            }
        }
    }

    private fun setItemDecoration(){
        albumsRecyclerView.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL_LIST))
    }


}