package com.wandm.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Environment
import android.support.v7.app.AlertDialog
import com.wandm.R
import java.io.File

class StorageSelectDialog(context: Context) : DialogInterface.OnClickListener {

    private val mDialog: AlertDialog
    private val mStorages: Array<File>
    private var mDirSelectListener: OnDirSelectListener? = null

    init {
        mStorages = getAvailableStorage(context)
        val names = arrayOfNulls<String>(mStorages.size)
        for (i in mStorages.indices) {
            names[i] = mStorages[i].name
        }
        mDialog = AlertDialog.Builder(context)
                .setItems(names, this)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.menu_show_as_entry_default) { _, _ ->
                    mDirSelectListener!!.onDirSelected(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
                }
                .setCancelable(true)
                .setTitle(R.string.select_storage)
                .create()
    }

    fun setDirSelectListener(dirSelectListener: OnDirSelectListener): StorageSelectDialog {
        this.mDirSelectListener = dirSelectListener
        return this
    }

    fun show() {
        mDialog.show()
    }

    override fun onClick(dialogInterface: DialogInterface, position: Int) {
        val dir = mStorages[position]
        mDirSelectListener!!.onDirSelected(dir)
    }


    private fun getAvailableStorage(context: Context): Array<File> {
        val storageRoot = File("/storage")
        return storageRoot.listFiles { file -> file.canRead() }
    }

    interface OnDirSelectListener {
        fun onDirSelected(dir: File)
    }
}