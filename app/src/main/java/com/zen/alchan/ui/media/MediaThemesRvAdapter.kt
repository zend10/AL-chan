package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.response.AnimeTheme
import com.zen.alchan.data.response.AnimeThemeEntry
import com.zen.alchan.databinding.ListMediaThemeBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.TextRvAdapter

class MediaThemesRvAdapter(
    private val context: Context,
    list: List<AnimeTheme>,
    private val listener: MediaThemesListener
) : BaseRecyclerViewAdapter<AnimeTheme, ListMediaThemeBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaThemeBinding) : ViewHolder(binding) {
        override fun bind(item: AnimeTheme, index: Int) {
            with(binding) {
                themeTitle.text = item.getDisplayTitle()
                themeTitle.clicks {
                    item.themeEntries.firstOrNull()?.let {
                        listener.openThemeDialog(item, it)
                    }
                }
                themeVersionRecyclerView.show(item.themeEntries.size > 1)
                themeVersionRecyclerView.adapter = TextRvAdapter(
                    context,
                    item.themeEntries.map { it.getDisplayTitle() },
                    object : TextRvAdapter.TextListener {
                        override fun getText(text: String) {
                            var selectedIndex = item.themeEntries.indexOfFirst { it.getDisplayTitle() == text }
                            if (selectedIndex == -1)
                                selectedIndex = 0
                            listener.openThemeDialog(item, item.themeEntries[selectedIndex])
                        }
                    }
                )
            }
        }
    }

    interface MediaThemesListener {
        fun openThemeDialog(animeTheme: AnimeTheme, animeThemeEntry: AnimeThemeEntry)
    }
}