package com.zen.alchan.ui.staff.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.CharacterEdge
import com.zen.alchan.data.response.anilist.Media
import com.zen.databinding.ListCardImageAndTextBinding
import com.zen.databinding.ListLoadingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class StaffCharacterListRvAdapter(
    private val context: Context,
    list: List<CharacterEdge?>,
    private val appSetting: AppSetting,
    private val listener: StaffCharacterListListener
) : BaseRecyclerViewAdapter<CharacterEdge?, ViewBinding>(list) {

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
        override fun bind(item: CharacterEdge?, index: Int) {
            if (item == null)
                return

            binding.apply {
                ImageUtil.loadImage(context, item.node.getImage(appSetting), cardImage)
                cardText.text = item.node.name.userPreferred
                cardText.setLines(2)
                cardText.maxLines = 2

                cardSubtitle.text = item.role?.name?.convertFromSnakeCase(false)
                cardSubtitle.setLines(2)
                cardSubtitle.maxLines = 2

                cardRecyclerView.adapter = StaffCharacterListMediaRvAdapter(context, item.media.take(3), appSetting, listener)
                cardRecyclerView.show(item.media.isNotEmpty())

                root.clicks { listener.navigateToCharacter(item.node) }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: CharacterEdge?, index: Int) {
            // do nothing
        }
    }
}