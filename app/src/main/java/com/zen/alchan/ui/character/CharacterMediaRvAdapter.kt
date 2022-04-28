package com.zen.alchan.ui.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.alchan.databinding.ListCardImageAndTextBinding
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.databinding.ListMediaRelationBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class CharacterMediaRvAdapter(
    private val context: Context,
    list: List<MediaEdge?>,
    private val appSetting: AppSetting,
    private val listener: CharacterListener.CharacterMediaListener
) : BaseRecyclerViewAdapter<MediaEdge?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListCardImageAndTextBinding.inflate(inflater, parent, false)
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

                cardSubtitle.text = item.getCharacterRoleString()
                cardSubtitle.setLines(1)
                cardSubtitle.maxLines = 1

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