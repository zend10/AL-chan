package com.zen.alchan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.response.Media
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.libs.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.GenreRvAdapter
import kotlinx.android.synthetic.main.list_trending_media.view.*
import type.MediaType

class TrendingMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val width: Int,
    private val listener: HomeListener.TrendingMediaListener
) : BaseRecyclerViewAdapter<Media>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_trending_media, parent, false)
        view.layoutParams.width = (width.toDouble() / 1.3).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(context, list[position])
            holder.itemView.setOnClickListener { listener.navigateToMedia() }
        }
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(context: Context, media: Media) {
            ImageUtil.loadImage(context, media.bannerImage, view.trendingBannerImage)
            ImageUtil.loadImage(context, media.coverImage.extraLarge, view.trendingCoverImage)

            view.trendingMediaTitleText.text = media.title.userPreferred
            view.trendingMediaProducerText.text = if (media.type == MediaType.ANIME) {
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
            view.trendingMediaScoreText.text = media.averageScore.getNumberFormatting()
            view.trendingMediaFavouriteText.text = media.favourites.getNumberFormatting()

            view.trendingMediaGenreRecyclerView.adapter = GenreRvAdapter(context, media.genres)
            (view.trendingMediaGenreRecyclerView.layoutManager as? FlexboxLayoutManager)?.maxLine = 1

            view.trendingMediaDescriptionText.text = HtmlCompat.fromHtml(media.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }
}