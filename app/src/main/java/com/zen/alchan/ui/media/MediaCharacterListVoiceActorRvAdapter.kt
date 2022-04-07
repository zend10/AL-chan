package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.StaffRoleType
import com.zen.alchan.databinding.ListMediaCharacterVoiceActorBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaCharacterListVoiceActorRvAdapter(
    private val context: Context,
    list: List<StaffRoleType>,
    private val appSetting: AppSetting,
    private val listener: MediaCharacterListRvAdapter.MediaCharacterListListener
) : BaseRecyclerViewAdapter<StaffRoleType, ListMediaCharacterVoiceActorBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaCharacterVoiceActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaCharacterVoiceActorBinding) : ViewHolder(binding) {
        override fun bind(item: StaffRoleType, index: Int) {
            binding.apply {
                voiceActorName.text = item.voiceActor.name.userPreferred
                voiceActorRoleNote.text = "(${item.roleNote})"
                voiceActorRoleNote.show(item.roleNote.isNotBlank())
                voiceActorDubGroup.text = item.dubGroup
                voiceActorDubGroup.show(item.dubGroup.isNotBlank())
                ImageUtil.loadCircleImage(context, item.voiceActor.getImage(appSetting), voiceActorImage)

                root.clicks { listener.navigateToStaff(item.voiceActor) }
            }
        }
    }
}