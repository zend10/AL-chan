package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.databinding.ListCircularBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaCharacterRvAdapter(
    private val context: Context,
    list: List<Character>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener.MediaCharacterListener
) : BaseRecyclerViewAdapter<Character, ListCircularBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / 5).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCircularBinding) : ViewHolder(binding) {
        override fun bind(item: Character, index: Int) {
            binding.apply {
                val image = if (appSetting.useHighestQualityImage)
                    item.image.large
                else
                    item.image.medium

                ImageUtil.loadCircleImage(context, image, circularItemImage)
                circularItemText.text = item.name.userPreferred

                root.clicks { listener.navigateToCharacter(item) }
            }
        }
    }
}