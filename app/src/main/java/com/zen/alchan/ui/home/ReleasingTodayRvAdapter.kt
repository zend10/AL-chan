package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.databinding.ListMediaListGridBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.ReleasingTodayItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.MediaType
import kotlin.math.abs

class ReleasingTodayRvAdapter(
    private val context: Context,
    list: List<ReleasingTodayItem>,
    private val appSetting: AppSetting,
    private val listener: HomeListener.ReleasingTodayListener
) : BaseRecyclerViewAdapter<ReleasingTodayItem, ListMediaListGridBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaListGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaListGridBinding) : ViewHolder(binding) {
        override fun bind(item: ReleasingTodayItem, index: Int) {
            with(binding) {
                val mediaList = item.mediaList
                val media = mediaList.media

                ImageUtil.loadImage(context, media.getCoverImage(appSetting), mediaListCoverImage)

                mediaListTitleText.text = media.getTitle(appSetting)

                mediaListFormatText.text = if (item.timeUntilAiring >= 0) {
                    if (item.timeUntilAiring >= 3600) {
                        context.getString(R.string.ep_x_in_y_hours, item.episode, item.timeUntilAiring / 3600)
                    } else {
                        context.getString(R.string.ep_x_in_y_minutes, item.episode, item.timeUntilAiring / 60)
                    }
                } else {
                    if (item.timeUntilAiring <= -3600) {
                        context.getString(R.string.ep_x_y_hours_ago, item.episode, abs(item.timeUntilAiring) / 3600)
                    } else {
                        context.getString(R.string.ep_x_y_minutes_ago, item.episode, abs(item.timeUntilAiring) / 60)
                    }
                }
                mediaListProgressText.text = "${mediaList.progress} / ${mediaList.media.episodes ?: "?"}"

                mediaListAiringRootLayout.show(false)
                mediaListScoreLayout.show(false)
                mediaListProgressVolumeLayout.show(false)

                root.clicks {
                    listener.navigateToListEditor(mediaList)
                }

                mediaListTitleLayout.clicks {
                    listener.navigateToMedia(media)
                }

                mediaListProgressLayout.clicks {
                    listener.showProgressDialog(mediaList)
                }
            }
        }
    }
}