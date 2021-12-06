package com.zen.alchan.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.ListRectangleBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class FavoriteMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val mediaType: MediaType,
    private val appSetting: AppSetting,
    private val listener: ProfileListener.FavoriteMediaListener
) : BaseRecyclerViewAdapter<Media, ListRectangleBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListRectangleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListRectangleBinding) : ViewHolder(binding) {
        override fun bind(item: Media, index: Int) {
            binding.apply {
                val image = if (appSetting.useHighestQualityImage)
                    item.coverImage.extraLarge
                else
                    item.coverImage.large

                ImageUtil.loadImage(context, image, rectangleItemImage)
                rectangleItemText.show(false)
                root.clicks { listener.navigateToMedia(item, mediaType) }
            }
        }
    }
}