package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.LayoutHorizontalListBinding
import com.zen.alchan.databinding.LayoutTitleAndListBinding
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.MediaItem
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaRvAdapter(
    private val context: Context,
    list: List<MediaItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener
) : BaseRecyclerViewAdapter<MediaItem, ViewBinding>(list) {

    private var characterAdapter: MediaCharacterRvAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            MediaItem.VIEW_TYPE_SYNOPSIS -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return SynopsisViewHolder(view)
            }
            MediaItem.VIEW_TYPE_CHARACTERS -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                characterAdapter = MediaCharacterRvAdapter(context, listOf(), appSetting, width, listener.mediaCharacterListener)
                view.horizontalListRecyclerView.adapter = characterAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageBig)))
                return CharacterViewHolder(view)
            }
            else -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return SynopsisViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class SynopsisViewHolder(private val binding: LayoutTitleAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.apply {
                itemTitle.show(true)
                itemTitle.text = context.getString(R.string.synopsis)
                MarkdownUtil.applyMarkdown(context, itemText, item.media.description)
            }
        }
    }

    inner class CharacterViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.horizontalListTitle.text = context.getString(R.string.characters)
            binding.horizontalListSeeMore.clicks { listener.mediaCharacterListener.navigateToMediaCharacter(item.media) }
            characterAdapter?.updateData(item.media.characters.nodes)
        }
    }
}