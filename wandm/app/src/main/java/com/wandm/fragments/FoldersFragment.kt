package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.R
import com.wandm.adapters.FoldersAdapter
import com.wandm.loaders.MusicFoldersLoader
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_folders.*

class FoldersFragment : BaseFragment() {

    private var foldersAdapter: FoldersAdapter? = null

    override fun getLayoutResId() = R.layout.fragment_folders

    override fun onCreatedView(savedInstanceState: Bundle?) {
        setupViews()
        loadFolders()
    }

    private fun setupViews() {
        retainInstance = true
        foldersRecyclerView.layoutManager = LinearLayoutManager(activity)
        foldersRecyclerView.addItemDecoration(DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL_LIST))
        foldersFastScroller.setRecyclerView(foldersRecyclerView)
    }

    private fun loadFolders() {
        foldersAdapter = FoldersAdapter(ArrayList())
        foldersProgressBar.visibility = View.VISIBLE
        foldersRecyclerView.visibility = View.GONE
        foldersFastScroller.visibility = View.GONE

        MusicFoldersLoader.getMusicFolders(activity,
                MusicFoldersLoader.externalStorage) { mediaFolders ->
            if (mediaFolders != null) {
                foldersAdapter = FoldersAdapter(mediaFolders)
                foldersRecyclerView.adapter = foldersAdapter
                foldersRecyclerView.visibility = View.VISIBLE
                foldersFastScroller.visibility = View.VISIBLE
            }

            foldersProgressBar.visibility = View.GONE
        }
    }
}

