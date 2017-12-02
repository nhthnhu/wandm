package com.wandm.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.wandm.R
import com.wandm.adapters.FoldersAdapter
import com.wandm.loaders.MusicFoldersLoader
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_folders.*

class FoldersFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private val TAG = "FoldersFragment"
    }

    private var foldersAdapter: FoldersAdapter = FoldersAdapter(ArrayList())

    override fun getLayoutResId() = R.layout.fragment_folders

    override fun onCreatedView(savedInstanceState: Bundle?) {
        setupViews()
        loadFolders(false)
    }

    override fun onRefresh() {
        container.isRefreshing = true
        loadFolders(true)
    }

    private fun setupViews() {
        foldersRecyclerView.layoutManager = LinearLayoutManager(activity)
        foldersRecyclerView.addItemDecoration(DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL_LIST))
        foldersFastScroller.setRecyclerView(foldersRecyclerView)
        container.setOnRefreshListener(this)
    }

    private fun loadFolders(loadingAgain: Boolean) {
        foldersProgressBar.visibility = View.VISIBLE
        foldersRecyclerView.visibility = View.GONE
        foldersFastScroller.visibility = View.GONE

        if (activity != null) {
            MusicFoldersLoader.getMusicFolders(activity,
                    MusicFoldersLoader.externalStorage, loadingAgain) { mediaFolders ->

                try {
                    container.isRefreshing = false
                    if (mediaFolders != null) {
                        foldersAdapter.musicFolders = mediaFolders
                        foldersRecyclerView.adapter = foldersAdapter
                        foldersRecyclerView.visibility = View.VISIBLE
                        foldersFastScroller.visibility = View.VISIBLE
                    }
                    foldersProgressBar.visibility = View.GONE
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
                Log.d(TAG, "Folders loading completed")
            }
        }
    }
}

