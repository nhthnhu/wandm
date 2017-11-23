package com.wandm.models

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

class SongSearchSuggestion : SearchSuggestion {

    var title: String
    var isHistory = false

    override fun getBody() = title

    constructor(title: String, isHistory: Boolean = false) {
        this.title = title
        this.isHistory = isHistory
    }

    constructor(parcel: Parcel) {
        title = parcel.readString()
        isHistory = parcel.readInt() != 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(if (isHistory) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SongSearchSuggestion> {
        override fun createFromParcel(parcel: Parcel): SongSearchSuggestion {
            return SongSearchSuggestion(parcel)
        }

        override fun newArray(size: Int): Array<SongSearchSuggestion?> {
            return arrayOfNulls(size)
        }
    }

}