package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.ListMediaTrendingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.GenreRvAdapter
import com.zen.alchan.type.MediaType

class TrendingMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: HomeListener.TrendingMediaListener
) : BaseRecyclerViewAdapter<Media, ListMediaTrendingBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / 1.3).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaTrendingBinding) : ViewHolder(binding) {
        override fun bind(item: Media, index: Int) {
            binding.apply {
                ImageUtil.loadImage(context, item.bannerImage, trendingBannerImage)
                ImageUtil.loadImage(context, item.getCoverImage(appSetting), trendingCoverImage)

                trendingMediaTitleText.text = item.getTitle(appSetting)
                trendingMediaProducerText.text = if (item.type == MediaType.ANIME) {
                    item.studios.edges
                        .filter { it.isMain }
                        .joinToString(", ") { it.node.name }
                } else {
                    item.getMainStaff()
                        .joinToString(", ") { it.node.name.full }
                }
                trendingMediaScoreText.text = item.averageScore.getNumberFormatting()
                trendingMediaFavouriteText.text = item.favourites.getNumberFormatting()

                trendingMediaGenreRecyclerView.adapter = GenreRvAdapter(context, item.genres.take(3)) // taking only 3 to avoid text getting cut off
                (trendingMediaGenreRecyclerView.layoutManager as? FlexboxLayoutManager)?.maxLine = 1

                trendingMediaDescriptionText.text = HtmlCompat.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

                root.clicks { listener.navigateToMedia(item) }
            }
        }
    }
}