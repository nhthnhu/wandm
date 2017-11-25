package com.wandm.adapters

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wandm.R
import com.wandm.dialogs.SongFolderDetailDialog
import com.wandm.models.MusicFolder
import com.wandm.utils.PreferencesUtils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_folder.view.*


@Suppress("INACCESSIBLE_TYPE")
class FoldersAdapter(var musicFolders: List<MusicFolder>) : RecyclerView.Adapter<FoldersAdapter.ItemHolder>(), BubbleTextGetter {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FoldersAdapter.ItemHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_folder, viewGroup, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(itemHolder: FoldersAdapter.ItemHolder, i: Int) {
        itemHolder.bind(musicFolders[i])
    }

    override fun getItemCount() = musicFolders.size

    override fun getTextToShowInBubble(pos: Int): String {
        if (musicFolders.isEmpty())
            return ""

        try {
            val f = musicFolders[pos].file
            return if (f.isDirectory) {
                f.name[0].toString()
            } else {
                Character.toString(f.name[0])
            }
        } catch (e: Exception) {
            return ""
        }
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(musicFolder: MusicFolder) {

            val context = itemView.context

            if (PreferencesUtils.getLightTheme()) {
                itemView.folderImage.setColor(context.resources.getColor(R.color.color_light_theme))
            } else {
                itemView.folderImage.setColor(context.resources.getColor(R.color.color_dark_theme))
            }

            itemView.folderName.text = musicFolder.file.name

            val pathText = context.resources?.getQuantityString(
                    R.plurals.song_count,
                    musicFolder.songs.size,
                    musicFolder.songs.size)

            itemView.folderPath.text = pathText + " | " + musicFolder.file.path

            itemView.setOnClickListener {
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                val songFolderDetailDialog = SongFolderDetailDialog.newInstance(musicFolder)
                songFolderDetailDialog.show(fragmentManager, SongFolderDetailDialog::class.java.name)
            }
        }
    }

}