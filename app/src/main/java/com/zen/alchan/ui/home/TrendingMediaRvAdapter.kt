package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.ListTrendingMediaBinding
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.GenreRvAdapter
import type.MediaType

class TrendingMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val width: Int,
    private val listener: HomeListener.TrendingMediaListener
) : BaseRecyclerViewAdapter<Media>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTrendingMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / 1.3).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(context, list[position])
            holder.itemView.setOnClickListener { listener.navigateToMedia() }
        }
    }

    class ViewHolder(private val binding: ListTrendingMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, media: Media) {
            binding.apply {
                ImageUtil.loadImage(context, media.bannerImage, trendingBannerImage)
                ImageUtil.loadImage(context, media.coverImage.extraLarge, trendingCoverImage)

                trendingMediaTitleText.text = media.title.userPreferred
                trendingMediaProducerText.text = if (media.type == MediaType.ANIME) {
                    media.studios.edges
                        .filter { it.isMain }
                        .joinToString(", ") { it.node.name }
                } else {
                    media.staffs.edges
                        .filter {
                            it.role.contains("art", true) ||
                                    it.role.contains("story", true) ||
                                    it.role.contains("creator", true) ||
                                    it.role.contains("original", true)
                        }
                        .joinToString(", ") { it.node.name.full }
                }
                trendingMediaScoreText.text = media.averageScore.getNumberFormatting()
                trendingMediaFavouriteText.text = media.favourites.getNumberFormatting()

                trendingMediaGenreRecyclerView.adapter = GenreRvAdapter(context, media.genres)
                (trendingMediaGenreRecyclerView.layoutManager as? FlexboxLayoutManager)?.maxLine = 1

                trendingMediaDescriptionText.text = HtmlCompat.fromHtml(media.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }
    }
}