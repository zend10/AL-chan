package com.zen.alchan.ui.staff.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.ListStaffCharacterMediaBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.staff.StaffListener

class StaffCharacterListMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val appSetting: AppSetting,
    private val listener: StaffCharacterListListener
) : BaseRecyclerViewAdapter<Media, ListStaffCharacterMediaBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListStaffCharacterMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListStaffCharacterMediaBinding) : ViewHolder(binding) {
        override fun bind(item: Media, index: Int) {
            binding.apply {
                mediaName.text = item.getTitle(appSetting)
                ImageUtil.loadRectangleImage(context, item.getCoverImage(appSetting), mediaCoverImage)
                root.clicks { listener.navigateToMedia(item) }
            }
        }
    }
}