package com.zen.alchan.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.databinding.ListCircularBinding
import com.zen.alchan.databinding.ListRectangleBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class FavoriteStaffRvAdapter(
    private val context: Context,
    list: List<Staff>,
    private val listener: ProfileListener.FavoriteStaffListener
) : BaseRecyclerViewAdapter<Staff, ListRectangleBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListRectangleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListRectangleBinding) : ViewHolder(binding) {
        override fun bind(item: Staff, index: Int) {
            binding.apply {
                val image = item.image.large
                ImageUtil.loadImage(context, image, rectangleItemImage)
                rectangleItemText.show(false)
                root.clicks { listener.navigateToStaff(item) }
            }
        }
    }
}