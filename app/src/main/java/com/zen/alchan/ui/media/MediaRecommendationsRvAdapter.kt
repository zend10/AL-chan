package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Recommendation
import com.zen.alchan.databinding.ListMediaRecommendationBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.MediaStatus

class MediaRecommendationsRvAdapter(
    private val context: Context,
    list: List<Recommendation>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener.MediaRecommendationsListener
) : BaseRecyclerViewAdapter<Recommendation, ListMediaRecommendationBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / 1.3).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaRecommendationBinding) : ViewHolder(binding) {
        override fun bind(item: Recommendation, index: Int) {
            binding.apply {
                item.mediaRecommendation?.let {
                    ImageUtil.loadImage(context, it.getCoverImage(appSetting), recommendationCoverImage)
                    recommendationTitleText.text = it.getTitle(appSetting)
                    recommendationYearText.text = it.startDate?.year?.toString() ?: "TBA"
                    recommendationYearText.show(it.startDate?.year != null || it.status == MediaStatus.NOT_YET_RELEASED)
                    recommendationFormatText.text = it.getFormattedMediaFormat(true)
                    recommendationLengthText.text = it.getLength()?.showUnit(context, if (it.type?.getMediaType() == MediaType.ANIME) R.plurals.episode else R.plurals.chapter)
                    recommendationLengthText.show(it.getLength() != null && it.getLength() != 0)
                    recommendationLengthDividerIcon.show(it.getLength() != null && it.getLength() != 0)

                    recommendationRatingText.text = item.rating.getNumberFormatting()
                    recommendationScoreText.text = it.averageScore.getNumberFormatting()
                    recommendationFavoriteText.text = it.favourites.getNumberFormatting()
                    recommendationCardBackground.clicks {
                        listener.navigateToMedia(it)
                    }
                }
            }
        }
    }
}