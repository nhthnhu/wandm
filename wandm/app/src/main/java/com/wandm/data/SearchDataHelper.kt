package com.wandm.data

import android.content.Context
import android.widget.Filter
import com.wandm.models.SongSearchSuggestion
import com.wandm.models.song.Song
import java.util.*

object SearchDataHelper {

    private var songSuggestions = ArrayList<SongSearchSuggestion>()
    private var historySuggestions = ArrayList<SongSearchSuggestion>()

    fun setSongSuggestions(songs: ArrayList<Song>, update: Boolean = false) {
        if (songSuggestions.size == 0) {
            for (song in songs) {
                songSuggestions.add(SongSearchSuggestion(song.title))
            }
        } else {
            if (update) {
                songSuggestions.clear()
                for (song in songs) {
                    songSuggestions.add(SongSearchSuggestion(song.title))
                }
            }
        }
    }

    fun addHistory(title: String) {
        historySuggestions
                .filter { it.title.toUpperCase() == title.toUpperCase() }
                .forEach { return }
        historySuggestions.add(SongSearchSuggestion(title, true))
    }

    fun getHistorySuggestions() = historySuggestions

    fun clearHistory() {
        historySuggestions.clear()
    }

    fun findSuggestions(context: Context, query: String, limit: Int, simulatedDelay: Long,
                        listener: OnFindSuggestionsListener?) {
        object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                try {
                    Thread.sleep(simulatedDelay)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                val suggestionList = ArrayList<SongSearchSuggestion>()
                if (!(constraint == null || constraint.length == 0)) {

                    for (suggestion in songSuggestions) {
                        if (suggestion.body
                                .toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion)
                            if (limit != -1 && suggestionList.size == limit) {
                                break
                            }
                        }
                    }
                }

                val results = FilterResults()
                Collections.sort(suggestionList) { lhs, rhs -> if (lhs.isHistory) -1 else 0 }
                results.values = suggestionList
                results.count = suggestionList.size

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                if (listener != null) {
                    listener.onResults(results.values as List<SongSearchSuggestion>)
                }
            }
        }.filter(query)

    }

    interface OnFindSuggestionsListener {
        fun onResults(results: List<SongSearchSuggestion>)
    }
}