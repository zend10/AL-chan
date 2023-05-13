package com.zen.alchan.ui.media.themes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.ListBottomSheetMediaThemeBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.ThemeItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class BottomSheetMediaThemeRvAdapter(
    private val context: Context,
    list: List<ListItem<ThemeItem>>,
    private val listener: BottomSheetMediaThemesDialog.BottomSheetMediaThemeListener
) : BaseRecyclerViewAdapter<ListItem<ThemeItem>, ListBottomSheetMediaThemeBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListBottomSheetMediaThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListBottomSheetMediaThemeBinding) : ViewHolder(binding) {
        override fun bind(item: ListItem<ThemeItem>, index: Int) {
            with(binding) {
                themeTitle.text = if (item.stringResources.isNotEmpty())
                    context.getString(item.stringResources.first())
                else
                    item.text

                ImageUtil.loadImage(
                    context,
                    when (item.data.viewType) {
                        ThemeItem.VIEW_TYPE_VIDEO -> R.drawable.ic_baseline_music_video
                        ThemeItem.VIEW_TYPE_AUDIO -> R.drawable.ic_baseline_audiotrack
                        ThemeItem.VIEW_TYPE_YOUTUBE -> R.drawable.ic_youtube_play_button
                        ThemeItem.VIEW_TYPE_SPOTIFY -> R.drawable.ic_spotify
                        else -> 0
                    },
                    themeIcon
                )

                root.isEnabled = item.data.viewType != ThemeItem.VIEW_TYPE_TEXT

                root.clicks {
                    if (item.data.viewType == ThemeItem.VIEW_TYPE_VIDEO || item.data.viewType == ThemeItem.VIEW_TYPE_AUDIO) {
                        listener.playWithPlayer(item.data.url)
                    } else {
                        listener.playWithOtherApp(item.data.searchQuery)
                    }
                }
            }
        }
    }
}