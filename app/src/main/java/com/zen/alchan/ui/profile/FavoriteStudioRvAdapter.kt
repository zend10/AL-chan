package com.zen.alchan.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.Studio
import com.zen.alchan.databinding.ListCardTextBinding
import com.zen.alchan.databinding.ListCircularBinding
import com.zen.alchan.databinding.ListRectangleBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class FavoriteStudioRvAdapter(
    private val context: Context,
    list: List<Studio>,
    private val listener: ProfileListener.FavoriteStudioListener
) : BaseRecyclerViewAdapter<Studio, ListCardTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCardTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCardTextBinding) : ViewHolder(binding) {
        override fun bind(item: Studio, index: Int) {
            binding.apply {
                cardIcon.show(false)
                cardText.text = item.name
                root.clicks { listener.navigateToStudio(item) }
            }
        }
    }
}