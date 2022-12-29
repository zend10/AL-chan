package com.zen.alchan.ui.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.CharacterEdge
import com.zen.alchan.databinding.ListCircularBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class StaffCharacterRvAdapter(
    private val context: Context,
    list: List<CharacterEdge>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: StaffListener.StaffCharacterListener
) : BaseRecyclerViewAdapter<CharacterEdge, ListCircularBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / context.resources.getInteger(R.integer.horizontalListCharacterDivider)).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCircularBinding) : ViewHolder(binding) {
        override fun bind(item: CharacterEdge, index: Int) {
            binding.apply {
                ImageUtil.loadCircleImage(context, item.node.getImage(appSetting), circularItemImage)
                circularItemText.text = item.node.name.userPreferred

                root.clicks { listener.navigateToCharacter(item.node) }
            }
        }
    }
}