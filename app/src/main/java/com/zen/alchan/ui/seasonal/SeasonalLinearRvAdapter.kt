package com.zen.alchan.ui.seasonal

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.ListMediaListLinearBinding
import com.zen.alchan.databinding.ListSeasonalLinearBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.SeasonalItem
import com.zen.alchan.helper.utils.ImageUtil

class SeasonalLinearRvAdapter(
    context: Context,
    list: List<SeasonalItem>,
    appSetting: AppSetting,
    listener: SeasonalListener
) : BaseSeasonalRvAdapter(context, list, appSetting, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SeasonalItem.VIEW_TYPE_TITLE -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                TitleViewHolder(view)
            }
            else -> {
                val view = ListSeasonalLinearBinding.inflate(inflater, parent, false)
                ListItemViewHolder(view)
            }
        }
    }

    inner class ListItemViewHolder(private val binding: ListSeasonalLinearBinding) : ItemViewHolder(binding) {
        override fun bind(item: SeasonalItem, index: Int) {
            val media = item.media
            with(binding) {
                ImageUtil.loadImage(context, getCoverImage(media), seasonalImage)
                seasonalTitle.text = getTitle(media)
                seasonalStudio.text = media.studios.edges.firstOrNull()?.node?.name ?: ""
                seasonalScoreText.text = media.averageScore.getNumberFormatting()
                seasonalFavouriteText.text = media.favourites.getNumberFormatting()
                seasonalPopularityText.text = media.popularity.getNumberFormatting()

                seasonalMediaListStatusLayout.isEnabled = media.mediaListEntry == null
                ImageUtil.loadImage(context, getMediaListStatusIcon(media), seasonalMediaListStatusIcon)

                val statusColor = getMediaListStatusColor(media)
                seasonalMediaListStatusIcon.imageTintList = ColorStateList.valueOf(statusColor)
                seasonalMediaListStatusText.setTextColor(statusColor)
                seasonalMediaListStatusText.text = getMediaListStatusText(media)

                root.clicks {
                    listener.navigateToMedia(media)
                }

                root.setOnLongClickListener {
                    listener.showQuickDetail(media)
                    true
                }

                seasonalMediaListStatusLayout.clicks {
                    listener.addToPlanning(media)
                }
            }
        }
    }
}