package com.zen.alchan.ui.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.StaffRoleType
import com.zen.alchan.databinding.ListCircularBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class CharacterVoiceActorRvAdapter(
    private val context: Context,
    list: List<StaffRoleType>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: CharacterListener
) : BaseRecyclerViewAdapter<StaffRoleType, ListCircularBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / 5).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCircularBinding) : ViewHolder(binding) {
        override fun bind(item: StaffRoleType, index: Int) {
            binding.apply {
                ImageUtil.loadCircleImage(context, item.voiceActor.getImage(appSetting), circularItemImage)
                circularItemText.text = item.voiceActor.name.userPreferred
                circularItemText.setLines(1)
                circularItemText.maxLines = 1

                circularItemDescriptionText.text = item.voiceActor.language
                circularItemDescriptionText.show(true)
                circularItemDescriptionText.setLines(1)
                circularItemDescriptionText.maxLines = 1

                root.clicks {
                    listener.navigateToStaff(item.voiceActor)
                }

                root.setOnLongClickListener {
                    listener.showStaffMedia(item.voiceActor)
                    true
                }
            }
        }
    }
}