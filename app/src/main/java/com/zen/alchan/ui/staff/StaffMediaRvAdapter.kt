package com.zen.alchan.ui.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.databinding.ListCardImageAndTextBinding
import com.zen.databinding.ListLoadingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class StaffMediaRvAdapter(
    private val context: Context,
    list: List<MediaEdge?>,
    private val appSetting: AppSetting,
    private val width: Int?,
    private val listener: StaffListener.StaffMediaListener
) : BaseRecyclerViewAdapter<MediaEdge?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                if (width != null)
                    view.root.layoutParams.width = (width.toDouble() / context.resources.getInteger(R.integer.horizontalListRelationDivider)).toInt()
                LoadingViewHolder(view)
            }
             else -> {
                 val view = ListCardImageAndTextBinding.inflate(inflater, parent, false)
                 if (width != null)
                     view.root.layoutParams.width = (width.toDouble() / context.resources.getInteger(R.integer.horizontalListRelationDivider)).toInt()
                 ItemViewHolder(view)
             }
        }
    }

    inner class ItemViewHolder(private val binding: ListCardImageAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaEdge?, index: Int) {
            if (item == null)
                return

            binding.apply {
                ImageUtil.loadImage(context, item.node.getCoverImage(appSetting), cardImage)

                cardText.text = item.node.getTitle(appSetting)
                cardText.setLines(2)
                cardText.maxLines = 2

                cardSubtitle.text = item.staffRole
                cardSubtitle.setLines(2)
                cardSubtitle.maxLines = 2

                cardInfoLayout.show(true)
                cardInfoText.text = item.node.getFormattedMediaFormat(true)

                cardRecyclerView.show(false)

                root.clicks {
                    listener.navigateToMedia(item.node)
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: MediaEdge?, index: Int) {
            // do nothing
        }
    }
}