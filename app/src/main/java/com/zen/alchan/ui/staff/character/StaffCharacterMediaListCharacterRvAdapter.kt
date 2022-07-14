package com.zen.alchan.ui.staff.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.databinding.ListMediaCharacterVoiceActorBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class StaffCharacterMediaListCharacterRvAdapter(
    private val context: Context,
    list: List<Character>,
    private val appSetting: AppSetting,
    private val listener: StaffCharacterListListener
) : BaseRecyclerViewAdapter<Character, ListMediaCharacterVoiceActorBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaCharacterVoiceActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaCharacterVoiceActorBinding) : ViewHolder(binding) {
        override fun bind(item: Character, index: Int) {
            binding.apply {
                voiceActorName.text = item.name.userPreferred
                voiceActorRoleNote.show(false)
                voiceActorDubGroup.show(false)
                ImageUtil.loadCircleImage(context, item.getImage(appSetting), voiceActorImage)
                root.clicks { listener.navigateToCharacter(item) }
            }
        }
    }
}