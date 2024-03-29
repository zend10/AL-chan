package com.zen.alchan.ui.media.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Staff
import com.zen.alchan.data.response.anilist.StaffEdge
import com.zen.alchan.databinding.ListCardImageAndTextBinding
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaStaffListRvAdapter(
    private val context: Context,
    list: List<StaffEdge?>,
    private val appSetting: AppSetting,
    private val listener: MediaStaffListListener
): BaseRecyclerViewAdapter<StaffEdge?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListCardImageAndTextBinding.inflate(inflater, parent, false)
                view.cardRecyclerView.addItemDecoration(SpaceItemDecoration(bottom = context.resources.getDimensionPixelSize(R.dimen.marginSmall)))
                ItemViewHolder(view)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ListCardImageAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: StaffEdge?, index: Int) {
            if (item == null)
                return

            binding.apply {
                ImageUtil.loadImage(context, item.node.getImage(appSetting), cardImage)

                cardText.text = item.node.name.userPreferred
                cardText.setLines(2)
                cardText.maxLines = 2

                cardSubtitle.text = item.role
                cardSubtitle.setLines(2)
                cardSubtitle.maxLines = 2

                cardRecyclerView.show(false)

                root.clicks { listener.navigateToStaff(item.node) }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: StaffEdge?, index: Int) {
            // do nothing
        }
    }



    interface MediaStaffListListener {
        fun navigateToStaff(staff: Staff)
    }
}